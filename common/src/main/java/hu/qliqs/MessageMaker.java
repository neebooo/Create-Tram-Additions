package hu.qliqs;

import com.simibubi.create.content.trains.entity.Train;
import de.mrjulsen.crn.data.TrainGroup;
import de.mrjulsen.crn.data.TrainLine;
import de.mrjulsen.crn.data.storage.GlobalSettings;
import de.mrjulsen.crn.data.train.TrainListener;
import hu.qliqs.instructions.AnnounceInstruction;
import hu.qliqs.mixin_interfaces.*;

import java.util.UUID;
import java.util.regex.Pattern;

public class MessageMaker {
    public static String stationNameToTag(String stationName) throws NoClassDefFoundError {
        return GlobalSettings.getInstance().getOrCreateStationTagFor(stationName).getTagName().get();
    }

    public static String uuidToLine(UUID uuid,int scheduleIndex) {
        TrainLine trainLine = TrainListener.data.get(uuid).getTrainInfo(scheduleIndex).line();
        if (trainLine == null) {
            return "Unknown";
        }
        return trainLine.getLineName();
    }

    public static String uuidToGroup(UUID uuid,int scheduleIndex) {
        TrainGroup trainGroup = TrainListener.data.get(uuid).getTrainInfo(scheduleIndex).group();
        if (trainGroup == null) {
            return "Unknown";
        }
        return trainGroup.getGroupName();
    }

    public static String makeStationName(String stationName) {
        try {
            stationName = stationNameToTag(stationName);
        } catch (NoClassDefFoundError ignored) {}
        return stationName;
    }

    public static String formatMessage(String message,Train train) {
        if (train.id == null || train.runtime == null || train.runtime.getSchedule() == null){
            return message;
        }

        String next_stop = AnnounceInstruction.getNextStop(train);

        message = message.replaceAll(Pattern.quote("${next_stop}"), makeStationName(next_stop));
        try {
            message = message.replaceAll(Pattern.quote("${line}"), uuidToLine(train.id,train.runtime.getSchedule().savedProgress));
            message = message.replaceAll(Pattern.quote("${group}"),uuidToGroup(train.id,train.runtime.getSchedule().savedProgress));
        } catch (NoClassDefFoundError | NullPointerException ignored) {}

        return message;
    }

    public static String makeMessage(UUID stationUUID, String stationName, Boolean arrived, Train train) {

        stationName = makeStationName(stationName);

        if (arrived) {
            return "%s.".formatted(stationName);
        }

        TrainACInterface trainAC = ((TrainACInterface) train);

        if (trainAC.createTramAdditions$getOmitNextStopAnnouncement()) {
            trainAC.createTramAdditions$setOmitNextStopAnnouncement(false);
            return "";
        }

        return formatMessage(trainAC.createTramAdditions$getDefaultNextStopAnnouncement(), train);
    }
}
