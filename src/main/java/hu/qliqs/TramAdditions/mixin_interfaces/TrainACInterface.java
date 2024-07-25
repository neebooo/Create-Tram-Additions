package hu.qliqs.TramAdditions.mixin_interfaces;

import org.spongepowered.asm.mixin.Unique;

public interface TrainACInterface {
    String createTramAdditions$getChangeHereString();
    void  createTramAdditions$setChangeHereString(String value);
    boolean createTramAdditions$getOmitNextStopAnnouncement();
    void createTramAdditions$setOmitNextStopAnnouncement(boolean value);
}
