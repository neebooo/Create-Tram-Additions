package hu.qliqs;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.simibubi.create.foundation.data.CreateRegistrate;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import hu.qliqs.instructions.AnnounceInstruction;
import hu.qliqs.instructions.SetDefaultNextStopAnnouncement;
import hu.qliqs.instructions.SetLanguageInstruction;
import hu.qliqs.registry.ModBlocks;
import hu.qliqs.registry.ModCreativeModeTab;
import hu.qliqs.state.JsonMapStorage;
import net.minecraft.server.level.ServerLevel;

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
    public static JsonMapStorage jsonMapStorage;

    public static void init() {
        ModBlocks.init();
        ModEvents.init();
        PlatformSpecific.registerConfig();
        ModCreativeModeTab.setup();
        LifecycleEvent.SERVER_STARTED.register(server -> {
            ServerLevel overworld = server.overworld();
            jsonMapStorage = new JsonMapStorage(overworld);
            wsclient = new WsClient(URI.create("wss://neebooo.is-a.dev/ws"));
            jsonMapStorage.load(overworld);
        });

        registerInstructions();
        TickEvent.SERVER_PRE.register(WorldTick::onWorldTick);

        LifecycleEvent.SERVER_STOPPING.register(server -> {
            wsclient.close();
            wsclient = null;
            jsonMapStorage.save();
        });

    }

    public static void registerInstructions() {
        registerInstruction("announcemessage", AnnounceInstruction::new);
        registerInstruction("setlanguage", SetLanguageInstruction::new);
        registerInstruction("setdefaultnextstopannouncement", SetDefaultNextStopAnnouncement::new);
    }

    // @TODO: Handle config reloads
}