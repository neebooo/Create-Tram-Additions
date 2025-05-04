package hu.qliqs.fabric;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import fuzs.forgeconfigapiport.impl.config.ForgeConfigRegistryImpl;
import hu.qliqs.TramAdditions;
import hu.qliqs.config.ModClientConfig;
import hu.qliqs.config.ModServerConfig;
import io.github.fabricators_of_create.porting_lib.util.ServerLifecycleHooks;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.config.ModConfig;

public class PlatformSpecificImpl {
    public static MinecraftServer getServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }
    public static void registerConfig() {
        if (Platform.getEnvironment() == Env.CLIENT) {
            ForgeConfigRegistryImpl.INSTANCE.register(TramAdditions.MOD_ID, ModConfig.Type.CLIENT, ModClientConfig.SPEC, TramAdditions.MOD_ID + "-client.toml");
        }
        ForgeConfigRegistryImpl.INSTANCE.register(TramAdditions.MOD_ID, ModConfig.Type.SERVER, ModServerConfig.SPEC, TramAdditions.MOD_ID + "-server.toml");
    }
}
