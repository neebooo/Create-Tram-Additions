package hu.qliqs.TramAdditions.forge.Network.Packets;

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

    public static void handleThreaded(AnnouncePacket packet) throws IOException, UnsupportedAudioFileException {
        while (!doneAnnouncing) {
            Thread.onSpinWait();
        }
        doneAnnouncing = false;
        TTS tts = new TTS();
        final VoiceRole[] roles = VoiceRole.byLocale ("en-GB").toArray (new VoiceRole[0]);
        VoiceRole voice = null;
        for (VoiceRole role : roles) {
            if (role.nickname == "Sonia") {
                voice = role;
                break;
            }
        }
        if (voice == null) {
            voice = roles[0];
        }
        tts.config().oneShot().voice(voice);

        tts.synthesis(packet.message + "               "); // Theres a weird bug when it stops at the very last letters this is why I am appending spaces
        doneAnnouncing = true;
    }
}
