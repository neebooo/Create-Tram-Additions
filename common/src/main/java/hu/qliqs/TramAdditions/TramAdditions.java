package hu.qliqs.TramAdditions;

import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.simibubi.create.content.trains.schedule.Schedule.INSTRUCTION_TYPES;

public final class TramAdditions {
    public static final String MOD_ID = "create_tram_additions";
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);

    public static final Map<String, Map<String, String>> translationMapServer = Map.of( // This is not possible to put in a normal resource file because the server can't access it!
            "en-us", Map.of(
                    "next_station", "The next station is %s",
                    "line_to", "This is line %s towards %s"
            ),
            "uk-ua", Map.of(
                    "next_station", "Наступна зупинка %s",
                    "line_to", "Це маршрут з %s до %s"
            ),
            "de-de", Map.of(
                    "next_station", "Nächste Station %s",
                    "line_to", "Willkommen im %s nach %s"
            ),
            "hu-hu", Map.of(
                    "next_station", "A következő megálló %s",
                    "line_to", "Ez a %s, %s fele"
            )
    );

    public static void init() {
        // Write common init code here.
    }

    public static void registerInstruction(String name, Supplier<? extends ScheduleInstruction> factory) {
        INSTRUCTION_TYPES.add(Pair.of(new ResourceLocation(MOD_ID, name), factory));
    }

}
