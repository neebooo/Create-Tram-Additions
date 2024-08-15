package hu.qliqs.TramAdditions;

import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

import static com.simibubi.create.content.trains.schedule.Schedule.INSTRUCTION_TYPES;

public final class TramAdditions {
    public static final String MOD_ID = "create_tram_additions";
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);

    public static void init() {
        // Write common init code here.
    }

    public static void registerInstruction(String name, Supplier<? extends ScheduleInstruction> factory) {
        INSTRUCTION_TYPES.add(Pair.of(new ResourceLocation(MOD_ID,name), factory));
    }

}
