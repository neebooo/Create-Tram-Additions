/*
* Thank you Steam n' rails :3333 (Hippity hoppity your code is now my property <:)
*/

package hu.qliqs.TramAdditions.mixin;

import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.ScheduleEntry;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import com.simibubi.create.content.trains.station.GlobalStation;
import hu.qliqs.TramAdditions.ICustomExecutableInstruction;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ScheduleRuntime.class, remap = false)
public abstract class MixinScheduleRuntime {
    @Shadow
    Schedule schedule;

    @Shadow public int currentEntry;

    @Shadow public ScheduleRuntime.State state;

    @Shadow public boolean isAutoSchedule;

    @Shadow public abstract void discardSchedule();

    @Shadow Train train;

    @Inject(method = "startCurrentInstruction", at = @At("HEAD"), cancellable = true)
    private void startCustomInstruction(CallbackInfoReturnable<GlobalStation> cir) {
        if (schedule.entries.size() < currentEntry) return;

        ScheduleEntry entry = schedule.entries.get(currentEntry);
        ScheduleInstruction instruction = entry.instruction;

        if (instruction instanceof ICustomExecutableInstruction customExecutableInstruction) {
            cir.setReturnValue(customExecutableInstruction.executeWithStation((ScheduleRuntime) (Object) this));
        }
    }

    @Inject(method = "tickConditions", at = @At("HEAD"), cancellable = true)
    private void tickWhenNoConditions(Level level, CallbackInfo ci) {
        if (schedule.entries.get(currentEntry).conditions.isEmpty()) {
            state = ScheduleRuntime.State.PRE_TRANSIT;
            currentEntry++;
            ci.cancel();
        }
    }
}