package hu.qliqs.TramAdditions.forge.Network.Packets;

import hu.qliqs.TramAdditions.forge.TramAdditions;
import hu.qliqs.TramAdditions.forge.Utils;
import javazoom.jl.converter.Converter;
import javazoom.jl.decoder.JavaLayerException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.dreamwork.tools.tts.TTS;
import org.dreamwork.tools.tts.VoiceRole;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Supplier;

public class AnnouncePacket {
    private final String message;
    private final String language;

    public AnnouncePacket(String message, String voiceRole) {
        this.message = message;
        this.language = voiceRole;
    }

    public static void encode(AnnouncePacket packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.message);
        buf.writeUtf(packet.language);
    }

    public static AnnouncePacket decode(FriendlyByteBuf buf) {
        return new AnnouncePacket(buf.readUtf(32767),buf.readUtf(32767));
    }

    public static volatile Boolean doneAnnouncing = true;

    public static boolean handle(AnnouncePacket packet, Supplier<NetworkEvent.Context> supplier) {
        // We are on the client
        NetworkEvent.Context context = supplier.get();
        Thread thread = new Thread(() -> {
            try {
                handleThreaded(packet);
            } catch (IOException | UnsupportedAudioFileException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        return true;
    }

    public static void handleThreaded(AnnouncePacket packet) throws IOException, UnsupportedAudioFileException, InterruptedException {
        while (!doneAnnouncing) {
            Thread.onSpinWait();
        }
        doneAnnouncing = false;

        TramAdditions.getTTS().config().voice(VoiceRole.valueOf(packet.language));

        TramAdditions.getTTS().synthesis(packet.message);
        doneAnnouncing = true;
    }
}
