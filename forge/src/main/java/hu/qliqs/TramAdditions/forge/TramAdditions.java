package hu.qliqs.TramAdditions.forge;

import dev.architectury.platform.forge.EventBuses;
import hu.qliqs.TramAdditions.forge.Network.ModMessages;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(hu.qliqs.TramAdditions.TramAdditions.MOD_ID)
public final class TramAdditions {
    public TramAdditions() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(hu.qliqs.TramAdditions.TramAdditions.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        IEventBus modEventBus = EventBuses.getModEventBus(hu.qliqs.TramAdditions.TramAdditions.MOD_ID).get();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        hu.qliqs.TramAdditions.TramAdditions.init();
    }
    private void commonSetup(final FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            hu.qliqs.TramAdditions.TramAdditions.registerInstruction("announcemessage", AnnounceInstruction::new);
            hu.qliqs.TramAdditions.TramAdditions.registerInstruction("nextstationinfo", NextStationInstruction::new);
            ModMessages.register();
        });
    }
}
