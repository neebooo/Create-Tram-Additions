package hu.qliqs.registry;

import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.util.entry.BlockEntry;
import hu.qliqs.TramAdditions;
import hu.qliqs.blocks.FloorBlock;
import net.minecraft.client.renderer.RenderType;

public class ModBlocks {

    public static final BlockEntry<FloorBlock> FLOOR_BLOCK = TramAdditions.REGISTRATE.block("floor_block", FloorBlock::new)
            .addLayer(() -> RenderType::cutout)
            .initialProperties(SharedProperties::softMetal)
            .transform(TagGen.pickaxeOnly())
            .item()
            .tab(ModCreativeModeTab.MAIN_TAB.getKey())
            .build()
            .register();

    public static void init() {
    }
}
