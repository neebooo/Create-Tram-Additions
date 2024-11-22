package hu.qliqs.TramAdditions.forge;

import com.google.common.eventbus.Subscribe;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.station.GlobalStation;
import de.mrjulsen.crn.data.StationTag;
import de.mrjulsen.crn.data.TrainExitSide;
import de.mrjulsen.crn.data.TrainGroup;
import de.mrjulsen.crn.data.TrainLine;
import de.mrjulsen.crn.data.storage.GlobalSettings;
import de.mrjulsen.crn.data.train.TrainListener;
import de.mrjulsen.crn.data.train.TrainUtils;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.platform.forge.EventBuses;
import hu.qliqs.TramAdditions.forge.Network.ModMessages;
import hu.qliqs.TramAdditions.forge.Network.Packets.AnnouncePacket;
import hu.qliqs.TramAdditions.forge.blocks.ModBlocks;
import hu.qliqs.TramAdditions.forge.items.ModCreativeModeTabs;
import hu.qliqs.TramAdditions.forge.items.ModItems;
import hu.qliqs.TramAdditions.mixin_interfaces.TrainACInterface;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.java_websocket.client.WebSocketClient;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

@Mod(hu.qliqs.TramAdditions.TramAdditions.MOD_ID)
public final class TramAdditions {

    public static Map<UUID, Boolean> hasAnnouncedNextStation = new HashMap<>();
    public static Map<UUID, Boolean> hasAnnouncedCurrentStation = new HashMap<>();

    public static ForgeConfigSpec.BooleanValue renderCoupling;
    public static ForgeConfigSpec.ConfigValue<String> apiEndpoint;

    public static void registerClientConfig(ForgeConfigSpec.Builder CLIENT_BUILDER) {
        CLIENT_BUILDER.comment("Render Settings").push("render");
        renderCoupling = CLIENT_BUILDER.comment("Choose to render the cable between the carriages or not (Useful for low floored trams)").define("Render Couplings",true);
    }

    public static void registerServerConfig(ForgeConfigSpec.Builder SERVER_BUILDER) {
        SERVER_BUILDER.comment("TTS Settings").push("tts-server");
        apiEndpoint = SERVER_BUILDER.comment("The API endpoint for the TTS to get the voice from.").define("TTS Endpoint","https://neebooo.is-a.dev");
    }

    public TramAdditions() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(hu.qliqs.TramAdditions.TramAdditions.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        IEventBus modEventBus = EventBuses.getModEventBus(hu.qliqs.TramAdditions.TramAdditions.MOD_ID).get();

        ModCreativeModeTabs.register(modEventBus);
        Config.register();
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);


        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onConfigChange);

        MinecraftForge.EVENT_BUS.register(this);
        hu.qliqs.TramAdditions.TramAdditions.init();
    }

    private void commonSetup(final FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            hu.qliqs.TramAdditions.TramAdditions.registerInstruction("announcemessage", AnnounceInstruction::new);
            hu.qliqs.TramAdditions.TramAdditions.registerInstruction("setlanguage", SetLanguageInstruction::new);
            hu.qliqs.TramAdditions.TramAdditions.registerInstruction("setdefaultnextstopannouncement",SetDefaultNextStopAnnouncement::new);
            TickEvent.SERVER_PRE.register((listener) -> {
                onWorldTick();
            });
            ModMessages.register();
        });
    }


    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModBlocks.floor_block.get());
        }
    }

    public static boolean isWaypointing(Train train){
        if (train == null || train.runtime == null || train.runtime.getSchedule() == null ){
            return false;
        }


        // Fix Crash
        if (train.runtime.getSchedule().entries.size() <= train.runtime.currentEntry) {
            return false;
        }

        return Objects.equals(train.runtime.getSchedule().entries.get(train.runtime.currentEntry).instruction.getId(), new ResourceLocation("railways", "waypoint_destination"));
    }

    @Subscribe
    public void onConfigChange(ModConfigEvent modConfigEvent) {
        hu.qliqs.TramAdditions.TramAdditions.RENDER_COUPLING = renderCoupling.get();
    }

    public static void onWorldTick() {
        Create.RAILWAYS.trains.values().forEach(train -> {
            if (!hasAnnouncedNextStation.containsKey(train.id)) {
                hasAnnouncedNextStation.put(train.id, false);
            }

            if (!hasAnnouncedCurrentStation.containsKey(train.id)) {
                hasAnnouncedCurrentStation.put(train.id, false);
            }

            TrainACInterface trainACI = (TrainACInterface) train;

            if (train.getCurrentStation() == null && !isWaypointing(train)) {
                hasAnnouncedCurrentStation.replace(train.id,false);
                if (train.navigation.destination != null && !hasAnnouncedNextStation.get(train.id)) {
                    train.carriages.forEach(carriage -> {
                        carriage.forEachPresentEntity(entity -> {
                            entity.getIndirectPassengers().forEach(p -> {
                                if (p instanceof Player) {
                                    String msg = makeMessage(train.navigation.destination.id, train.navigation.destination.name, false, train);
                                    if (msg.isEmpty()) {
                                        return;
                                    }
                                    ModMessages.sendToPlayer(new AnnouncePacket(msg,trainACI.createTramAdditions$getVoiceRole()), (ServerPlayer) p);
                                }
                            });
                        });
                    });
                    hasAnnouncedNextStation.replace(train.id, true);
                }
            } else {
                if (!hasAnnouncedCurrentStation.get(train.id) && !isWaypointing(train)) {
                    train.carriages.forEach(carriage -> {
                        carriage.forEachPresentEntity(entity -> {
                            entity.getIndirectPassengers().forEach(p -> {
                                if (p instanceof Player) {
                                    String msg = makeMessage(train.getCurrentStation().id, train.getCurrentStation().name, true, train);
                                    if (msg.isEmpty()) {
                                        return;
                                    }
                                    ModMessages.sendToPlayer(new AnnouncePacket(msg, trainACI.createTramAdditions$getVoiceRole()), (ServerPlayer) p);
                                }
                            });
                        });
                    });
                    hasAnnouncedCurrentStation.replace(train.id, true);
                }
                hasAnnouncedNextStation.replace(train.id, false);
            }
        });
    }

    public static String stationNameToTag(String stationName) throws NoClassDefFoundError {
        return GlobalSettings.getInstance().getOrCreateStationTagFor(stationName).getTagName().get();
    }

    public static String uuidToLine(UUID uuid,int scheduleIndex) {
        TrainLine trainLine = TrainListener.data.get(uuid).getTrainInfo(scheduleIndex).line();
        if (trainLine == null) {
            return "Unknown";
        }
        return trainLine.getLineName();
    }

    public static String uuidToGroup(UUID uuid,int scheduleIndex) {
        TrainGroup trainGroup = TrainListener.data.get(uuid).getTrainInfo(scheduleIndex).group();
        if (trainGroup == null) {
            return "Unknown";
        }
        return trainGroup.getGroupName();
    }

    public static String getDoorSide(Train train) {
        // Next stop
        GlobalStation nextStation = train.navigation.destination;
        if (nextStation == null) {
            return "Unknown";
        }

        return TrainUtils.getExitSide(nextStation).name();
    }

    public static String makeStationName(String stationName) {
        try {
            stationName = stationNameToTag(stationName);
        } catch (NoClassDefFoundError ignored) {}
        return stationName;
    }

    public static String formatMessage(String message,Train train) {
        if (train.id == null || train.runtime == null || train.runtime.getSchedule() == null){
            return message;
        }

        String next_stop = AnnounceInstruction.getNextStop(train);

        message = message.replaceAll(Pattern.quote("${next_stop}"), makeStationName(next_stop));
        try {
            message = message.replaceAll(Pattern.quote("${line}"), uuidToLine(train.id,train.runtime.getSchedule().savedProgress));
            message = message.replaceAll(Pattern.quote("${group}"),uuidToGroup(train.id,train.runtime.getSchedule().savedProgress));
            // message = message.replaceAll(Pattern.quote("${door_side}"), getDoorSide(train));
        } catch (NoClassDefFoundError ignored) {}

        return message;
    }

    public static String makeMessage(UUID stationUUID, String stationName, Boolean arrived, Train train) {

        stationName = makeStationName(stationName);

        if (arrived) {
            return "%s.".formatted(stationName);
        }

        TrainACInterface trainAC = ((TrainACInterface) train);

        if (trainAC.createTramAdditions$getOmitNextStopAnnouncement()) {
            trainAC.createTramAdditions$setOmitNextStopAnnouncement(false);
            return "";
        }

        return formatMessage(trainAC.createTramAdditions$getDefaultNextStopAnnouncement(), train);
    }
}
