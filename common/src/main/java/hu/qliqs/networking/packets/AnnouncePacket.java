package hu.qliqs.networking.packets;

import hu.qliqs.Utils;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
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
    public static volatile Boolean doneAnnouncing = true;

    public AnnouncePacket(FriendlyByteBuf buf) {
        this(buf.readUtf(32767), buf.readUtf(32767));
    }

    public AnnouncePacket(String message, String language) {
        this.message = message;
        this.language = language;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(message);
        buf.writeUtf(language);
    }

    public void apply(Supplier<NetworkManager.PacketContext> contextSupplier) {
        contextSupplier.get().queue(() -> {
            Thread thread = new Thread(() -> {
                try {
                    handleThreaded();
                } catch (IOException | UnsupportedAudioFileException | InterruptedException e) {
                    System.out.println("Got Exception");
                }
            });
            thread.start();
        });
    }

    private void handleThreaded() throws IOException, UnsupportedAudioFileException, InterruptedException {
        while (!doneAnnouncing) {
            Thread.onSpinWait();
        }
        doneAnnouncing = false;
        URL url = new URL(Utils.getAPIEndpoint() + "/?text=" + URLEncoder.encode(message, StandardCharsets.UTF_8) + "&lang=" + URLEncoder.encode(language, StandardCharsets.UTF_8));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        InputStream inputStream = con.getInputStream();
        InputStream bufferedIn = new BufferedInputStream(inputStream);
        AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedIn);
        Utils.playAudio(ais);
        doneAnnouncing = true;
    }
}
