package hu.qliqs.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModServerConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<String> TTS_SERVER;

    static {
        BUILDER.push("Create Tram Additions Config");
        TTS_SERVER = BUILDER.comment("The API endpoint for the TTS to get the voice from.")
                .define("TTS Endpoint", "https://neebooo.is-a.dev");
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
