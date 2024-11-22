package hu.qliqs.TramAdditions.forge;

import com.mojang.logging.LogUtils;
import hu.qliqs.TramAdditions.TramAdditions;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import java.util.Map;
import java.util.Set;

public class Utils {
    public static String getServerLocale(String locale_code, String key) {
        Map<String, String> translations = TramAdditions.translationMapServer.get(locale_code);
        if (translations != null) {
            String translated = translations.get(key);
            if (translated != null) {
                return translated;
            }
        } else {
            Set<String> locales = TramAdditions.translationMapServer.keySet();
            for (String locale : locales) {
                if (locale.startsWith(locale_code.split("-")[0])) {
                    return getServerLocale(locale,key);
                }
            }
            return getServerLocale("en-us",key); // For untranslated languages and stuff like en-gb
        }
        return "";
    }

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
