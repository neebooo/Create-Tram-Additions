package hu.qliqs.TramAdditions.forge;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import de.mrjulsen.crn.CreateRailwaysNavigator;
import de.mrjulsen.crn.data.GlobalSettingsManager;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.platform.forge.EventBuses;
import hu.qliqs.TramAdditions.forge.Network.ModMessages;
import hu.qliqs.TramAdditions.forge.Network.Packets.AnnouncePacket;
import hu.qliqs.TramAdditions.forge.blocks.ModBlocks;
import hu.qliqs.TramAdditions.forge.items.ModCreativeModeTabs;
import hu.qliqs.TramAdditions.forge.items.ModItems;
import hu.qliqs.TramAdditions.mixin_interfaces.TrainACInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.dreamwork.tools.tts.ITTSListener;
import org.dreamwork.tools.tts.TTS;
import org.dreamwork.tools.tts.VoiceRole;
import org.java_websocket.client.WebSocketClient;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Mod(hu.qliqs.TramAdditions.TramAdditions.MOD_ID)
public final class TramAdditions {

    public static Map<UUID, Boolean> hasAnnouncedNextStation = new HashMap<>();
    public static Map<UUID, Boolean> hasAnnouncedCurrentStation = new HashMap<>();

    public static TTS tts = null;

    public static boolean isTTSShutDown() {
        Field declaredField = null;
        try {
            declaredField = tts.getClass().getDeclaredField("client");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        declaredField.setAccessible(true);
        try {
            WebSocketClient client = (WebSocketClient) declaredField.get(tts);
            return client == null;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static TTS getTTS() {
            if (tts == null || isTTSShutDown()) {
                try {
                    tts = new TTS();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                tts.config()
                        .voice(VoiceRole.Sonia);
            }
            return tts;
    }

    public TramAdditions() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(hu.qliqs.TramAdditions.TramAdditions.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        IEventBus modEventBus = EventBuses.getModEventBus(hu.qliqs.TramAdditions.TramAdditions.MOD_ID).get();

        ModCreativeModeTabs.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);


        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        hu.qliqs.TramAdditions.TramAdditions.init();
    }

    private void commonSetup(final FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            hu.qliqs.TramAdditions.TramAdditions.registerInstruction("announcemessage", AnnounceInstruction::new);
            hu.qliqs.TramAdditions.TramAdditions.registerInstruction("nextstationinfo", NextStationInstruction::new);
            hu.qliqs.TramAdditions.TramAdditions.registerInstruction("setlanguage", SetLanguageInstruction::new);
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


    public static void onWorldTick() {
        Create.RAILWAYS.trains.values().forEach(train -> {
            if (!hasAnnouncedNextStation.containsKey(train.id)) {
                hasAnnouncedNextStation.put(train.id, false);
            }

            if (!hasAnnouncedCurrentStation.containsKey(train.id)) {
                hasAnnouncedCurrentStation.put(train.id, false);
            }

            /*
            if (isWaypointing(train)) {
                return;
            }
            This might work tho I have to test and I am lazy :3
             */

            if (train.getCurrentStation() == null && !isWaypointing(train)) {
                hasAnnouncedCurrentStation.replace(train.id,false);
                if (train.navigation.destination != null && !hasAnnouncedNextStation.get(train.id)) {
                    train.carriages.forEach(carriage -> {
                        carriage.forEachPresentEntity(entity -> {
                            entity.getIndirectPassengers().forEach(p -> {
                                if (p instanceof Player) {
                                    String msg = makeMessage(train.navigation.destination.name, false, train);
                                    if (msg.isEmpty()) {
                                        return;
                                    }
                                    ModMessages.sendToPlayer(new AnnouncePacket(msg), (ServerPlayer) p);
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
                                    String msg = makeMessage(train.getCurrentStation().name, true, train);
                                    if (msg.isEmpty()) {
                                        return;
                                    }
                                    ModMessages.sendToPlayer(new AnnouncePacket(msg), (ServerPlayer) p);
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

    public static String makeMessage(String stationName, Boolean arrived, Train train) {
        /*
        if (arrived) {
            return "%s.".formatted(stationName.replaceAll("\\d+$", ""));
        }
        */

        stationName = GlobalSettingsManager.getInstance().getSettingsData().getAliasFor("A").getAliasName().get();


        TrainACInterface trainAC = ((TrainACInterface) train);

        if (trainAC.createTramAdditions$getOmitNextStopAnnouncement()) {
            trainAC.createTramAdditions$setOmitNextStopAnnouncement(false);
            return "";
        }

        String additionalString = "";
        if (!Objects.equals(trainAC.createTramAdditions$getChangeHereString(), "")) {
            additionalString = "Change here for %s.".formatted(trainAC.createTramAdditions$getChangeHereString());
        }
        trainAC.createTramAdditions$setChangeHereString("");
        return "The next station is %s.%s".formatted(stationName.replaceAll("\\d+$", ""), additionalString);
    }
}
