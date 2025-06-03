package hu.qliqs;

import com.mojang.logging.LogUtils;
import com.simibubi.create.Create;
import hu.qliqs.mixin_interfaces.TrainACInterface;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static hu.qliqs.MessageMaker.makeMessage;
import static hu.qliqs.TramAdditions.*;
import static hu.qliqs.Utils.isWaypointing;

public class WorldTick {
    private static int tickCounter = 0;
    private static int reconnectCounter = 0;
    public static void onWorldTick(MinecraftServer server) {
        tickCounter++;
        if (tickCounter >= 20) {
            tickCounter = 0;

            ServerLevel world = server.overworld();
            long dayTime = world.getDayTime(); // Will be between 0 and 24000
            long normalizedTime = (dayTime + 6000) % 24000; // Shifting to make 0 = 6:00 AM

            int hours = (int)(normalizedTime / 1000);
            int minutes = (int)((normalizedTime % 1000) * 60 / 1000);

            String mcTime = String.format("%02d:%02d", hours, minutes); // formatted time

            try {
                wsclient.send("SEND|ALL|false|TIME;%s".formatted(mcTime)); // Send time to everyone!!!
            } catch (WebsocketNotConnectedException e) {
                System.out.println("The server isn't connected to the proxy.");
                reconnectCounter += 1;
                if(reconnectCounter > 5) {
                    reconnectCounter = 0;
                    userPinMap.clear();
                    wsclient = new WsClient(URI.create("wss://neebooo.is-a.dev/ws"));
                }
            }
        }
        Create.RAILWAYS.trains.values().forEach(train -> {
            if (!hasAnnouncedNextStation.containsKey(train.id)) {
                hasAnnouncedNextStation.put(train.id, false);
            }

            if (!hasAnnouncedCurrentStation.containsKey(train.id)) {
                hasAnnouncedCurrentStation.put(train.id, false);
            }

            TrainACInterface trainACI;
            try {
                trainACI = (TrainACInterface) train;
            } catch (ClassCastException cce) {
                LogUtils.getLogger().error(cce.getMessage());
                return;
            }

            if (train.getCurrentStation() == null && !isWaypointing(train)) {
                hasAnnouncedCurrentStation.replace(train.id,false);
                if (train.navigation.destination != null && !hasAnnouncedNextStation.get(train.id)) {
                    List<ServerPlayer> playerList = new ArrayList<>();
                    String msg = makeMessage(train.navigation.destination.id, train.navigation.destination.name, false, train);
                    if(msg.isEmpty()) {
                        return;
                    }
                    train.carriages.forEach(carriage -> {
                        carriage.forEachPresentEntity(entity -> {
                            entity.getIndirectPassengers().forEach(p -> {
                                if (p instanceof ServerPlayer) {
                                    playerList.add((ServerPlayer) p);
                                }
                            });
                        });
                    });
                    ServerPlayer[] serverPlayers = playerList.toArray(new ServerPlayer[0]);

                    hasAnnouncedNextStation.replace(train.id, true);
                    Utils.playSound(msg,trainACI.createTramAdditions$getVoiceRole(),serverPlayers);
                }
            } else {
                if (!hasAnnouncedCurrentStation.get(train.id) && !isWaypointing(train)) {
                    List<ServerPlayer> playerList = new ArrayList<>();
                    String msg = makeMessage(train.getCurrentStation().id, train.getCurrentStation().name, true, train);
                    if (msg.isEmpty()) {
                        return;
                    }

                    train.carriages.forEach(carriage -> {
                        carriage.forEachPresentEntity(entity -> {
                            entity.getIndirectPassengers().forEach(p -> {
                                if (p instanceof ServerPlayer) {
                                    playerList.add((ServerPlayer) p);
                                }
                            });
                        });
                    });
                    ServerPlayer[] serverPlayers = playerList.toArray(new ServerPlayer[0]);
                    hasAnnouncedCurrentStation.replace(train.id, true);
                    Utils.playSound(msg,trainACI.createTramAdditions$getVoiceRole(),serverPlayers);
                }
                hasAnnouncedNextStation.replace(train.id, false);
            }
        });
    }

}
