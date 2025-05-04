package hu.qliqs.forge;

import dev.architectury.platform.forge.EventBuses;
import hu.qliqs.commands.TramAdditionsCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import hu.qliqs.TramAdditions;

@Mod(TramAdditions.MOD_ID)
public final class TramAdditionsForge {
    public TramAdditionsForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(TramAdditions.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Run our common setup.
        TramAdditions.REGISTRATE.registerEventListeners(FMLJavaModLoadingContext.get().getModEventBus());
        TramAdditions.init();
    }
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        TramAdditionsCommand.register(event.getDispatcher());
    }
}
