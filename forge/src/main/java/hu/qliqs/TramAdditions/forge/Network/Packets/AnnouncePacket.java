package hu.qliqs.TramAdditions.forge.Network.Packets;

import hu.qliqs.TramAdditions.forge.TramAdditions;
import hu.qliqs.TramAdditions.forge.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
                // throw new RuntimeException(e);
                // Ignore
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
        URL url = new URL(TramAdditions.apiEndpoint.get() + "/?text=%s&lang=%s".formatted(URLEncoder.encode(packet.message, StandardCharsets.UTF_8),URLEncoder.encode(packet.language,StandardCharsets.UTF_8)));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        InputStream inputStream = con.getInputStream();
        InputStream bufferedIn = new BufferedInputStream(inputStream);
        AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedIn);
        Utils.playAudio(ais);
        doneAnnouncing = true;
    }
}
