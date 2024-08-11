package hu.qliqs.TramAdditions.forge;

import com.mojang.logging.LogUtils;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class Utils {
    public static void playAudio(AudioInputStream audio) {
        try {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audio.getFormat());
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(audio.getFormat());
            line.start();

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = audio.read(buffer, 0, buffer.length)) != -1) {
                line.write(buffer, 0, bytesRead);
            }

            line.drain();
            line.close();
        } catch (Exception e) {
            LogUtils.getLogger().error(e.getMessage());
        }
    }
}
