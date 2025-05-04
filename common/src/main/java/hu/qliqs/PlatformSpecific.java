package hu.qliqs;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.server.MinecraftServer;

public class PlatformSpecific {
    @ExpectPlatform
    public static void registerConfig() {
        throw new AssertionError();
    }
    @ExpectPlatform
    public static MinecraftServer getServer() {throw new AssertionError(); }
}
