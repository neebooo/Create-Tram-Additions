package hu.qliqs.TramAdditions.mixin_interfaces;

public interface TrainACInterface {
    String createTramAdditions$getChangeHereString();
    void  createTramAdditions$setChangeHereString(String value);
    boolean createTramAdditions$getOmitNextStopAnnouncement();
    void createTramAdditions$setOmitNextStopAnnouncement(boolean value);
    String createTramAdditions$getVoiceRole();
    void createTramAdditions$setVoiceRole(String voiceRole);
}
