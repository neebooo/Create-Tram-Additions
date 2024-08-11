package hu.qliqs.TramAdditions.forge;

import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.Pair;
import hu.qliqs.TramAdditions.ICustomExecutableInstruction;
import hu.qliqs.TramAdditions.TramAdditions;
import hu.qliqs.TramAdditions.mixin.AccessorScheduleRuntime;
import hu.qliqs.TramAdditions.mixin_interfaces.TrainACInterface;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class NextStationInstruction extends ScheduleInstruction implements ICustomExecutableInstruction {
    public NextStationInstruction() {
        data.putString("changehere","");
    }

    @Override
    public boolean supportsConditions() {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initConfigurationWidgets(ModularGuiLineBuilder builder) {
        builder.addTextInput(20,101,(si, l) -> {
            si.setMaxLength(32);
            si.setHint(Component.literal("Metro-line 1, 2 and 3"));
        },"changehere");
    }

    @Override
    public Pair<ItemStack, Component> getSummary() {
        return Pair.of(new ItemStack(Items.PAPER),Component.literal("Next Station Info"));
    }

    @Override
    public ResourceLocation getId() {
        return new ResourceLocation(TramAdditions.MOD_ID,"nextstationinfo");
    }

    @Override
    public void execute(ScheduleRuntime runtime) {
        TrainACInterface train = (TrainACInterface) ((AccessorScheduleRuntime)runtime).getTrain();
        train.createTramAdditions$setChangeHereString(textData("changehere"));
        runtime.state = ScheduleRuntime.State.PRE_TRANSIT;
        runtime.currentEntry++;
    }
}
