package hu.qliqs.TramAdditions;

import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.utility.Pair;
import hu.qliqs.TramAdditions.Network.ModMessages;
import hu.qliqs.TramAdditions.Network.Packets.AnnouncePacket;
import com.mojang.logging.LogUtils;
import com.simibubi.create.Create;
import hu.qliqs.TramAdditions.Sound.ModSounds;
import hu.qliqs.TramAdditions.mixin_interfaces.TrainACInterface;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Supplier;

import static com.simibubi.create.content.trains.schedule.Schedule.INSTRUCTION_TYPES;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TramAdditions.MODID)
@Mod.EventBusSubscriber()
public class TramAdditions {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "createtramadditions";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static Map<UUID, Boolean> hasAnnouncedNextStation = new HashMap<>();
    public static Map<UUID, Boolean> hasAnnouncedCurrentStation = new HashMap<>();

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

    public TramAdditions() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        ModSounds.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);
    }

    private static void registerInstruction(String name, Supplier<? extends ScheduleInstruction> factory) {
        INSTRUCTION_TYPES.add(Pair.of(new ResourceLocation(MODID,name), factory));
    }

    private void commonSetup(final FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            registerInstruction("announcemessage", AnnounceInstruction::new);
            registerInstruction("nextstationinfo", NextStationInstruction::new);
            ModMessages.register();
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.ServerTickEvent e) {
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
