package hu.qliqs.instructions;

import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.Pair;
import hu.qliqs.ICustomExecutableInstruction;
import hu.qliqs.TramAdditions;
import hu.qliqs.mixin.AccessorScheduleRuntime;
import hu.qliqs.mixin_interfaces.TrainACInterface;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SetDefaultNextStopAnnouncement extends ScheduleInstruction implements ICustomExecutableInstruction {
    public SetDefaultNextStopAnnouncement() {
        data.putString("announcement","");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void initConfigurationWidgets(ModularGuiLineBuilder builder) {
        builder.addTextInput(0,101,(si, l) -> {
            si.setMaxLength(1000); // technically the max length is 32767 chars, but I do not think anyone would realistically use that
        },"announcement");
    }


    @Override
    public boolean supportsConditions() {
        return false;
    }

    @Override
    public Pair<ItemStack, Component> getSummary() {
        return Pair.of(new ItemStack(Items.NOTE_BLOCK), Component.literal("Set default next stop announcement"));
    }

    @Override
    public ResourceLocation getId() {
        return new ResourceLocation(TramAdditions.MOD_ID,"setdefaultnextstopannouncement");
    }

    @Override
    public void execute(ScheduleRuntime runtime) {
        TrainACInterface train = (TrainACInterface) ((AccessorScheduleRuntime)runtime).getTrain();
        train.createTramAdditions$setDefaultNextStopAnnouncement(textData("announcement"));
        runtime.state = ScheduleRuntime.State.PRE_TRANSIT;
        runtime.currentEntry++;
    }
}
