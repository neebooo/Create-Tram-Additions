package hu.qliqs;

import com.mojang.logging.LogUtils;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import de.mrjulsen.dragnsounds.api.ServerApi;
import de.mrjulsen.dragnsounds.core.data.PlaybackConfig;
import de.mrjulsen.dragnsounds.core.ffmpeg.AudioSettings;
import de.mrjulsen.dragnsounds.core.ffmpeg.EChannels;
import hu.qliqs.config.ModServerConfig;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Supplier;
import static com.simibubi.create.content.trains.schedule.Schedule.INSTRUCTION_TYPES;


public class Utils {
    public static String getAPIEndpoint() {
        return ModServerConfig.TTS_SERVER.get();
    }

    public static void playSound(String message, String language, ServerPlayer[] players) {
        try {
            URL url = new URL(Utils.getAPIEndpoint() + "/?text=" + URLEncoder.encode(message, StandardCharsets.UTF_8) + "&lang=" + URLEncoder.encode(language, StandardCharsets.UTF_8));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            InputStream inputStream = con.getInputStream();
            InputStream bufferedIn = new BufferedInputStream(inputStream);
            ServerApi.playSoundOnce(bufferedIn,new AudioSettings(EChannels.STEREO,64000,22050,(byte)5), PlaybackConfig.defaultUI(1,1,0),players,(player, l, eSoundPlaybackStatus) -> {},null,statusResult -> {System.out.println(statusResult.message());});
        } catch (Exception e) {
            throw new RuntimeException(e);
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
