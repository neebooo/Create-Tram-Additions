package hu.qliqs.mixin_interfaces;

public interface TrainACInterface {
    boolean createTramAdditions$getOmitNextStopAnnouncement();
    void createTramAdditions$setOmitNextStopAnnouncement(boolean value);
    String createTramAdditions$getVoiceRole();
    void createTramAdditions$setVoiceRole(String voiceRole);
    String createTramAdditions$getDefaultNextStopAnnouncement();
    void createTramAdditions$setDefaultNextStopAnnouncement(String announcement);
}
