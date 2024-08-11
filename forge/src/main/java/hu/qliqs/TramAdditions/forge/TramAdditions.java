package hu.qliqs.TramAdditions.forge;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.platform.forge.EventBuses;
import hu.qliqs.TramAdditions.forge.Network.ModMessages;
import hu.qliqs.TramAdditions.forge.Network.Packets.AnnouncePacket;
import hu.qliqs.TramAdditions.mixin_interfaces.TrainACInterface;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Mod(hu.qliqs.TramAdditions.TramAdditions.MOD_ID)
public final class TramAdditions {

    public static Map<UUID, Boolean> hasAnnouncedNextStation = new HashMap<>();
    public static Map<UUID, Boolean> hasAnnouncedCurrentStation = new HashMap<>();

    public TramAdditions() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(hu.qliqs.TramAdditions.TramAdditions.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        IEventBus modEventBus = EventBuses.getModEventBus(hu.qliqs.TramAdditions.TramAdditions.MOD_ID).get();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        hu.qliqs.TramAdditions.TramAdditions.init();
    }
    private void commonSetup(final FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            hu.qliqs.TramAdditions.TramAdditions.registerInstruction("announcemessage", AnnounceInstruction::new);
            hu.qliqs.TramAdditions.TramAdditions.registerInstruction("nextstationinfo", NextStationInstruction::new);
            TickEvent.SERVER_PRE.register((listener) -> {
                onWorldTick();
            });
            ModMessages.register();
        });
    }

    public static void onWorldTick() {
        Create.RAILWAYS.trains.values().forEach(train -> {
            if (!hasAnnouncedNextStation.containsKey(train.id)) {
                hasAnnouncedNextStation.put(train.id,false);
            }

            if (!hasAnnouncedCurrentStation.containsKey(train.id)) {
                hasAnnouncedCurrentStation.put(train.id,false);
            }

            if (train.getCurrentStation() == null) {
                hasAnnouncedCurrentStation.replace(train.id,false);
                if (train.navigation.destination != null && !hasAnnouncedNextStation.get(train.id)) {
                    train.carriages.forEach(carriage -> {
                        carriage.forEachPresentEntity(entity -> {
                            entity.getIndirectPassengers().forEach(p -> {
                                if (p instanceof Player) {
                                    String msg = makeMessage(train.navigation.destination.name,false,train);
                                    if (msg.isEmpty()) {
                                        return;
                                    }
                                    ModMessages.sendToPlayer(new AnnouncePacket(msg), (ServerPlayer) p);
                                }
                            });
                        });
                    });
                    hasAnnouncedNextStation.replace(train.id,true);
                }
            } else {
                if (!hasAnnouncedCurrentStation.get(train.id)) {
                    train.carriages.forEach(carriage -> {
                        carriage.forEachPresentEntity(entity -> {
                            entity.getIndirectPassengers().forEach(p -> {
                                if (p instanceof Player) {
                                    String msg = makeMessage(train.getCurrentStation().name,true,train);
                                    if (msg.isEmpty()) {
                                        return;
                                    }
                                    ModMessages.sendToPlayer(new AnnouncePacket(msg), (ServerPlayer) p);
                                }
                            });
                        });
                    });
                    hasAnnouncedCurrentStation.replace(train.id,true);
                }
                hasAnnouncedNextStation.replace(train.id,false);
            }
        });
    }

    public static String makeMessage(String stationName, Boolean arrived, Train train) {
        if (arrived) {
            return "%s.".formatted(stationName.replaceAll("\\d+$",""));
        }

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
        return "The next station is %s.%s".formatted(stationName.replaceAll("\\d+$",""), additionalString);
    }
}
