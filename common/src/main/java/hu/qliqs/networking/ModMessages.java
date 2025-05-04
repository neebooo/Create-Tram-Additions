package hu.qliqs.networking;

import hu.qliqs.networking.packets.AnnouncePacket;
import net.minecraft.resources.ResourceLocation;
import dev.architectury.networking.NetworkChannel;
import net.minecraft.server.level.ServerPlayer;

public class ModMessages {
    private static final NetworkChannel CHANNEL = NetworkChannel.create(new ResourceLocation("tramadditions", "messages"));

    public static void register() {
        CHANNEL.register(AnnouncePacket.class, AnnouncePacket::encode, AnnouncePacket::new, AnnouncePacket::apply);
    }

    public static void sendToServer(AnnouncePacket message) {
        CHANNEL.sendToServer(message);
    }

    public static void sendToPlayer(AnnouncePacket message, ServerPlayer player) {
        CHANNEL.sendToPlayer(player, message);
    }
}
