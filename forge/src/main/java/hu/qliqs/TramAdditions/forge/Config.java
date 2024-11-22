package hu.qliqs.TramAdditions.forge;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class  Config {
    public static void register() {
        registerClientConfig();
        registerServerConfig();
    }

    private static void registerClientConfig() {
        ForgeConfigSpec.Builder ClientBuilder = new ForgeConfigSpec.Builder();
        TramAdditions.registerClientConfig(ClientBuilder);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientBuilder.build(),"tram-additions.toml");
    }

    private static void registerServerConfig() {
        ForgeConfigSpec.Builder ServerBuilder = new ForgeConfigSpec.Builder();
        TramAdditions.registerServerConfig(ServerBuilder);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER,ServerBuilder.build(),"tram-additions-server.toml");
    }


}
