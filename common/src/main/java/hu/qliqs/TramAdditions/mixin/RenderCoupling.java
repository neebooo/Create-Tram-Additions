package hu.qliqs.TramAdditions.mixin;

import com.simibubi.create.content.trains.entity.CarriageCouplingRenderer;
import hu.qliqs.TramAdditions.TramAdditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CarriageCouplingRenderer.class)
public class RenderCoupling {
    @Inject(method = "renderAll", at = @At("HEAD"), cancellable = true)
    private static void renderAll(CallbackInfo info) {
        if (!TramAdditions.RENDER_COUPLING) {
            info.cancel();
        }
    }
}
