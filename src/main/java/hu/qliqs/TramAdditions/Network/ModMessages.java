package hu.qliqs.TramAdditions.Network;

import hu.qliqs.TramAdditions.TramAdditions;
import hu.qliqs.TramAdditions.Network.Packets.AnnouncePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(TramAdditions.MODID, "messages")).networkProtocolVersion(() -> "1.0").clientAcceptedVersions(s -> true).serverAcceptedVersions(s -> true).simpleChannel();
        INSTANCE = net;
        net.messageBuilder(AnnouncePacket.class,id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(AnnouncePacket::decode)
                .encoder(AnnouncePacket::encode)
                .consumerMainThread(AnnouncePacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer sp) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> sp),message);
    }
}
