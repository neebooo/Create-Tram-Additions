package hu.qliqs.mixin;

import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ScheduleRuntime.class, remap = false)
public interface AccessorScheduleRuntime {
    @Accessor
    Train getTrain();
}