package hu.qliqs.TramAdditions.forge.Network.Packets;

import com.alibaba.fastjson.JSON;
import hu.qliqs.TramAdditions.forge.Utils;
import io.github.whitemagic2014.tts.TTS;
import io.github.whitemagic2014.tts.TTSVoice;
import io.github.whitemagic2014.tts.bean.Voice;
import javazoom.jl.converter.Converter;
import javazoom.jl.decoder.JavaLayerException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;
import java.util.function.Supplier;

public class AnnouncePacket {
    private final String message;

    public AnnouncePacket(String message) {
        this.message = message;
    }

    public static void encode(AnnouncePacket packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.message);
    }

    public static AnnouncePacket decode(FriendlyByteBuf buf) {
        return new AnnouncePacket(buf.readUtf(32767));
    }

    public static volatile Boolean doneAnnouncing = true;

    public static boolean handle(AnnouncePacket packet, Supplier<NetworkEvent.Context> supplier) {
        // We are on the client
        NetworkEvent.Context context = supplier.get();
        new Thread(() -> {
            try {
                handleThreaded(packet);
            } catch (IOException | UnsupportedAudioFileException e) {
                throw new RuntimeException(e);
            }
        }).start();
        return true;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void handleThreaded(AnnouncePacket packet) throws IOException, UnsupportedAudioFileException {
        JSON.parse("{}"); // @TODO: Fix this
        while (!doneAnnouncing) {
            Thread.onSpinWait();
        }
        doneAnnouncing = false;
        Voice voice = TTSVoice.provides().stream().filter(v -> v.getShortName().equals("en-GB-SoniaNeural")).findFirst().get();
        // remove %tmp%/tts.mp3
        String suffix = String.valueOf(new Random().nextInt(10) + 1);
        new TTS(voice,packet.message).fileName("tts" + suffix).formatMp3().storage(System.getProperty("java.io.tmpdir")).trans();
        String mp3path = Path.of(System.getProperty("java.io.tmpdir"),"tts%s.mp3".formatted(suffix)).toString();
        String wavePath = Path.of(System.getProperty("java.io.tmpdir"),"tts%s.wav".formatted(suffix)).toString();
        Converter converter = new Converter();
        try {
            converter.convert(mp3path,wavePath);
        } catch (JavaLayerException e) {
            new File(mp3path).delete();
            doneAnnouncing = true;
            return;
        }

        File waveFile = new File(wavePath);
        AudioInputStream ais = AudioSystem.getAudioInputStream(waveFile);
        Utils.playAudio(ais);
        // once its done responsibly delete the files
        new File(mp3path).delete();
        waveFile.delete();

        doneAnnouncing = true;
    }
}
