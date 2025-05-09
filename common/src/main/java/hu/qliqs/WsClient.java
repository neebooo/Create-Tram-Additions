package hu.qliqs;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import hu.qliqs.mixin_interfaces.TrainACInterface;
import hu.qliqs.networking.ModMessages;
import hu.qliqs.networking.packets.AnnouncePacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.*;

import static hu.qliqs.TramAdditions.*;

public class WsClient extends WebSocketClient {

    public WsClient(URI serverUri) {
        super(serverUri);
        this.connect();
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        this.send("joins");
    }

    @Override
    public void onMessage(String s) {
        String[] args = s.split("\\|");
        if (s.startsWith("JOINS")) {
            if (args.length == 1) {
                System.out.println("Received answer without status code or arguments");
            } else if (args.length == 2) {
                if (Objects.equals(args[1], "1")) {
                    System.out.println("Received answer without a SWUID");
                } else {
                    System.out.println("Received an error code without an error");
                }
            } else if (args.length == 3) {
                if (Objects.equals(args[1], "1")) {
                    TramAdditions.swuid = args[2];
                } else {
                    System.out.println("JOINS: " + args[2]);
                }
            }
        } else if (s.startsWith("AUTH")) {
            if (args.length == 1) {
                System.out.println("Received answer without status code or arguments");
            } else if (args.length == 2) {
                if (TramAdditions.userPinMap.containsValue(args[1])) {
                    this.send("AUTH|%s|success".formatted(args[1]));
                    PlatformSpecific.getServer().getPlayerList().getPlayerByName(TramAdditions.userPinMap.inverse().get(args[1])).sendSystemMessage(Component.literal("Paired successfully!"));
                } else {
                    this.send("AUTH|%s|invalid".formatted(args[1]));
                }
            }
        } else if (s.startsWith("GETSOUNDS")) {
            if (args.length == 1) {
                System.out.println("Received answer without pin");
            } else if(args.length == 2) {
                StringBuilder list = new StringBuilder();
                for(Map.Entry<String,String> entry : jsonMapStorage.getMap().entrySet()){
                    list.append("%s%s;%s".formatted(list.isEmpty() ? "" : ";", entry.getKey(), entry.getValue()));
                }
                this.send("SEND|%s|false|GETSOUNDS;%s".formatted(args[1],list.toString()));
            }
        } else if (s.startsWith("PLAYSOUND")) {
            if (args.length == 1) {
                System.out.println("Received answer without status code or arguments");
            } else if (args.length == 2) {
                System.out.println("Received pin without sound ID");
            } else if (args.length == 3) {
                try {
                Train train = Create.RAILWAYS.trains.get(driverTrainMap.get(userPinMap.inverse().get(args[1])));
                TrainACInterface trainACI = (TrainACInterface) train;
                train.carriages.forEach(carriage -> {
                    carriage.forEachPresentEntity(entity -> {
                        entity.getIndirectPassengers().forEach(p -> {
                            if (p instanceof Player) {
                                String msg = jsonMapStorage.getMap().get(args[2]);
                                if (msg.isEmpty()) {
                                    return;
                                }
                                ModMessages.sendToPlayer(new AnnouncePacket(msg, trainACI.createTramAdditions$getVoiceRole()), (ServerPlayer) p);
                            }
                        });
                    });
                });
                } catch(NullPointerException e) {
                    this.send("SEND|%s|false|PLAYSOUND;0;You are not driving a train".formatted(args[1]));
                }
            }
        } else if(s.startsWith("MSG")) {
            if(args.length < 3) {
                System.out.println("Received answer without status code or arguments");
            } else if(args.length == 3) {
                List<String> subargs = new ArrayList<>();
                Collections.addAll(subargs, args[2].split(";"));
                String command = subargs.remove(0).toLowerCase();
                if(command.equals("updatesounds")) {
                    jsonMapStorage.delete();
                    for (int i = 0; i < subargs.size(); i+=2) {
                        jsonMapStorage.update(subargs.get(i), subargs.get(i + 1));
                    }
                    jsonMapStorage.save();
                }
            }
        }
    }


    @Override
    public void onClose(int i, String s, boolean b) {
        System.out.println("Disconnected :c");
    }

    @Override
    public void onError(Exception e) {
        System.out.println(e.getMessage());
    }
}
