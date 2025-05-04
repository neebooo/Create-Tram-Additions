package hu.qliqs;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.simibubi.create.foundation.data.CreateRegistrate;
import dev.architectury.event.events.common.TickEvent;
import hu.qliqs.instructions.AnnounceInstruction;
import hu.qliqs.instructions.SetDefaultNextStopAnnouncement;
import hu.qliqs.instructions.SetLanguageInstruction;
import hu.qliqs.networking.ModMessages;
import hu.qliqs.registry.ModBlocks;
import hu.qliqs.registry.ModCreativeModeTab;
import hu.qliqs.state.JsonMapStorage;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static hu.qliqs.Utils.registerInstruction;

public final class TramAdditions {
    public static final String MOD_ID = "tram_additions";

    public static Map<UUID, Boolean> hasAnnouncedNextStation = new HashMap<>();
    public static Map<UUID, Boolean> hasAnnouncedCurrentStation = new HashMap<>();

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);
    public static WsClient wsclient = null;
    public static BiMap<String,String> userPinMap = HashBiMap.create();
    public static String swuid = "";
    public static HashMap<String, UUID> driverTrainMap = new HashMap<>();

    public static void init() {
        ModBlocks.init();
        PlatformSpecific.registerConfig();
        ModCreativeModeTab.setup();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            wsclient = new WsClient(URI.create("ws://0.0.0.0:8080"));
            ServerLevel overworld = server.overworld();
            JsonMapStorage.load(overworld);
        });

        registerInstructions();
        ModMessages.register();
        TickEvent.SERVER_PRE.register(WorldTick::onWorldTick);

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            ServerLevel overworld = server.overworld();
            JsonMapStorage.save(overworld);
        });

    }

    public static void registerInstructions() {
        registerInstruction("announcemessage", AnnounceInstruction::new);
        registerInstruction("setlanguage", SetLanguageInstruction::new);
        registerInstruction("setdefaultnextstopannouncement", SetDefaultNextStopAnnouncement::new);
    }

    // @TODO: Handle config reloads
}