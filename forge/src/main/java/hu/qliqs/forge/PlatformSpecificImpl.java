package hu.qliqs.forge;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import hu.qliqs.TramAdditions;
import hu.qliqs.config.ModClientConfig;
import hu.qliqs.config.ModServerConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.server.ServerLifecycleHooks;

public class PlatformSpecificImpl {
    public static MinecraftServer getServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }
    public static void registerConfig() {
        if (Platform.getEnvironment() == Env.CLIENT) {
            ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ModClientConfig.SPEC, TramAdditions.MOD_ID + "-client.toml");
        }
            ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ModServerConfig.SPEC, TramAdditions.MOD_ID + "-server.toml");
    }
}
