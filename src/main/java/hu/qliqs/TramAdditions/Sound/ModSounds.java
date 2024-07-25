package hu.qliqs.TramAdditions.Sound;

import hu.qliqs.TramAdditions.TramAdditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, TramAdditions.MODID);

    public static final RegistryObject<SoundEvent> TRAM_BELL_SOUND = registerSoundEvent("tram_bell");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TramAdditions.MODID,name)));
    }

    public static void register(IEventBus ebus) {
        SOUND_EVENTS.register(ebus);
    }
}
