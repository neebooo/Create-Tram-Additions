package hu.qliqs.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> RENDER_COUPLINGS;

    static {
        BUILDER.push("Create Tram Additions Config");
        RENDER_COUPLINGS = BUILDER.comment("Choose to render the cable between the carriages or not (Useful for low floored trams)")
                .define("Render Couplings", true);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

}
