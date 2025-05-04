package hu.qliqs.fabric;

import hu.qliqs.commands.TramAdditionsCommand;
import net.fabricmc.api.ModInitializer;

import hu.qliqs.TramAdditions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import static net.minecraft.commands.Commands.CommandSelection.DEDICATED;

public final class TramAdditionsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        // Run our common setup.
        TramAdditions.init();
        TramAdditions.REGISTRATE.register();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            TramAdditionsCommand.register(dispatcher);
        });
    }
}
