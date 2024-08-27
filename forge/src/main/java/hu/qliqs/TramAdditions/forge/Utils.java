package hu.qliqs.TramAdditions.forge;

import com.mojang.logging.LogUtils;
import hu.qliqs.TramAdditions.TramAdditions;
import net.minecraft.resources.ResourceLocation;
import org.dreamwork.tools.tts.VoiceRole;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import java.util.HashSet;
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
}
