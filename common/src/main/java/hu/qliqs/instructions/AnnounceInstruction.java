package hu.qliqs.instructions;

import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.destination.DestinationInstruction;
import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.Pair;
import hu.qliqs.ICustomExecutableInstruction;
import hu.qliqs.TramAdditions;
import hu.qliqs.mixin_interfaces.TrainACInterface;
import hu.qliqs.mixin.AccessorScheduleRuntime;
import hu.qliqs.networking.ModMessages;
import hu.qliqs.networking.packets.AnnouncePacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

import static hu.qliqs.MessageMaker.formatMessage;

public class AnnounceInstruction extends ScheduleInstruction implements ICustomExecutableInstruction {
    public String MsgToAnnounce;
    public AnnounceInstruction() {
        data.putString("Message","");
        data.putInt("OmitNextStop",0);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void initConfigurationWidgets(ModularGuiLineBuilder builder) {
        builder.addTextInput(0,101,(si, l) -> {
            si.setMaxLength(1000); // technically the max length is 32767 chars, but I do not think anyone would realistically use that
        },"Message");
        builder.addSelectionScrollInput(102,35,(si,l) -> {
            l.getToolTip().add(Component.literal("Omit next stop announcement"));
            si.forOptions(List.of(Component.literal("True"),Component.literal("False")));
        },"OmitNextStop");
    }

    @Override
    public boolean supportsConditions() {
        return false;
    }

    @Override
    public Pair<ItemStack, Component> getSummary() {
        return Pair.of(new ItemStack(Items.NOTE_BLOCK), Component.literal("Announce Message"));
    }

    @Override
    public ResourceLocation getId() {
        return new ResourceLocation(TramAdditions.MOD_ID,"announcemessage");
    }

    @Override
    public void execute(ScheduleRuntime runtime) {
        // @TODO: ADD FORMATMESSAGE FUNC IN ANOTHER PLACE THAN TRAMDDITIONS
        String finalMessage = formatMessage(textData("Message"), ((AccessorScheduleRuntime) runtime).getTrain());
        // Warning: True is 0, False is 1 in this case. Idk what im doing with my life but not something great
        TrainACInterface train = (TrainACInterface) ((AccessorScheduleRuntime) runtime).getTrain();
        train.createTramAdditions$setOmitNextStopAnnouncement((intData("OmitNextMessage") == 0));
        ((AccessorScheduleRuntime) runtime).getTrain().carriages.forEach(carriage -> {
            carriage.forEachPresentEntity(entity -> {
                entity.getIndirectPassengers().forEach(p -> {
                    if (p instanceof Player) {
                        ModMessages.sendToPlayer(new AnnouncePacket(finalMessage,train.createTramAdditions$getVoiceRole()), (ServerPlayer) p);
                    }
                });
            });
        });
        runtime.state = ScheduleRuntime.State.PRE_TRANSIT;
        runtime.currentEntry++;
    }

    private String getCurrentStop(Train train ){
        Schedule schedule = train.runtime.getSchedule();
        if (train.getCurrentStation() == null ){
            int startIndex = train.runtime.currentEntry - 1;
            for (int i = startIndex; i >= 0; i--) {
                if (schedule.entries.get(i).instruction instanceof  DestinationInstruction) {
                    return schedule.entries.get(i).instruction.getData().getString("Text");
                }
            }
            return "Error";
        }
        return train.getCurrentStation().name;
    }

    public static String getNextStop(Train train) {
        Schedule schedule = train.runtime.getSchedule();
        int startIndex = train.runtime.currentEntry;
        for (int i = startIndex; i < schedule.entries.size(); i++) {
            if (schedule.entries.get(i).instruction instanceof DestinationInstruction) {
                return schedule.entries.get(i).instruction.getData().getString("Text");
            }
        }
        for (int i = 0; i < schedule.entries.size(); i++) {
            if (schedule.entries.get(i).instruction instanceof DestinationInstruction) {
                return schedule.entries.get(i).instruction.getData().getString("Text");
            }
        }
        return "Nowhere";
    }
}
