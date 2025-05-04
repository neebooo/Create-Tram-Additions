package hu.qliqs.registry;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import hu.qliqs.TramAdditions;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeModeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(TramAdditions.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register(
            TramAdditions.MOD_ID, // Tab ID
            () -> CreativeTabRegistry.create(
                    Component.literal("Tram Additions"), // Tab Name
                    () -> new ItemStack(ModBlocks.FLOOR_BLOCK.asItem()) // Icon
            )
    );

    public static void setup() {
        CREATIVE_MODE_TABS.register();
    }
}