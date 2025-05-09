package hu.qliqs;

import com.mojang.logging.LogUtils;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import hu.qliqs.config.ModServerConfig;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import javax.sound.sampled.*;
import java.util.Objects;
import java.util.function.Supplier;
import static com.simibubi.create.content.trains.schedule.Schedule.INSTRUCTION_TYPES;


public class Utils {
    public static String getAPIEndpoint() {
        return ModServerConfig.TTS_SERVER.get();
    }

    public static void playAudio(AudioInputStream audioInputStream) {
        try {
            Clip clip = AudioSystem.getClip();

            clip.open(audioInputStream);
            clip.start();

        } catch (Exception e) {
            LogUtils.getLogger().error(e.getMessage());
        }
    }

    public static void registerInstruction(String name, Supplier<? extends ScheduleInstruction> factory) {
        INSTRUCTION_TYPES.add(Pair.of(new ResourceLocation(TramAdditions.MOD_ID, name), factory));
    }

    public static boolean isWaypointing(Train train){
        if (train == null || train.runtime == null || train.runtime.getSchedule() == null ){
            return false;
        }

        if (train.runtime.getSchedule().entries.size() <= train.runtime.currentEntry) {
            return false;
        }

        return Objects.equals(train.runtime.getSchedule().entries.get(train.runtime.currentEntry).instruction.getId(), new ResourceLocation("railways", "waypoint_destination"));
    }
}
