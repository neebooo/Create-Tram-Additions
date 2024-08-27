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
import org.dreamwork.tools.tts.VoiceRole;

import java.util.ArrayList;

public class SetLanguageInstruction extends ScheduleInstruction implements ICustomExecutableInstruction {
    public SetLanguageInstruction() {
        data.putString("ttslanguage", "");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initConfigurationWidgets(ModularGuiLineBuilder builder) {
        builder.addSelectionScrollInput(0, 101, (si, l) ->
        {
            l.getToolTip().add(Component.literal("Set announcer language to"));
            ArrayList<Component> options = new ArrayList<>();
            for (VoiceRole role : VoiceRole.values()) {
                options.add(Component.literal(role.locale + "(" + role.gender + ", " + role.nickname + ")"));
            }
            si.forOptions(options);
        }, "ttslanguage");
    }

    @Override
    public boolean supportsConditions() {
        return false;
    }

    @Override
    public Pair<ItemStack, Component> getSummary() {
        return Pair.of(new ItemStack(Items.PAPER), Component.literal("Set Announcer Language"));
    }

    @Override
    public ResourceLocation getId() {
        return new ResourceLocation(TramAdditions.MOD_ID,"setlanguage");
    }

    @Override
    public void execute(ScheduleRuntime runtime) {
        TrainACInterface train = (TrainACInterface) ((AccessorScheduleRuntime)runtime).getTrain();
        train.createTramAdditions$setVoiceRole(VoiceRole.values()[intData("ttslanguage")].toString());
        runtime.state = ScheduleRuntime.State.PRE_TRANSIT;
        runtime.currentEntry++;
    }
}
