package hu.qliqs.mixin;

import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.UUID;

import static hu.qliqs.TramAdditions.driverTrainMap;

@Mixin(CarriageContraptionEntity.class)
public class CarriageContraptionEntityMixin {

    @Shadow public UUID trainId;

    @Inject(
        method = "control",
        at = @At("HEAD")
    )
    private void onControl(BlockPos controlsLocalPos, Collection<Integer> heldControls, Player player, CallbackInfoReturnable<Boolean> cir) {
        driverTrainMap.put(player.getName().getString(),trainId);
    }
}