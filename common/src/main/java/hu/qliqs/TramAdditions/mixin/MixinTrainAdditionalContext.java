package hu.qliqs.TramAdditions.mixin;

import com.simibubi.create.content.trains.entity.Train;
import hu.qliqs.TramAdditions.mixin_interfaces.TrainACInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = Train.class,remap = false)
public class MixinTrainAdditionalContext implements TrainACInterface {
    @Unique
    public String createTramAdditions$changeHereString = "";

    @Unique
    public String createTramAdditions$getChangeHereString() {
        return createTramAdditions$changeHereString;
    }

    @Unique
    public void createTramAdditions$setChangeHereString(String value) {
        createTramAdditions$changeHereString = value;
    }

    @Unique
    public boolean createTramAdditions$omitNextStopAnnouncement = false;

    @Unique
    public boolean createTramAdditions$getOmitNextStopAnnouncement() {
        return createTramAdditions$omitNextStopAnnouncement;
    }

    @Unique
    public void createTramAdditions$setOmitNextStopAnnouncement(boolean value) {
        createTramAdditions$omitNextStopAnnouncement = value;
    }

    @Unique
    public String createTramAdditions$voiceRole = "Sonia";

    @Unique
    public String createTramAdditions$getVoiceRole() {return createTramAdditions$voiceRole;}

    @Unique
    public void createTramAdditions$setVoiceRole(String voiceRole) {createTramAdditions$voiceRole = voiceRole;}
}