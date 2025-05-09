package hu.qliqs.commands;

import com.mojang.brigadier.CommandDispatcher;
import hu.qliqs.TramAdditions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.Random;

public class TramAdditionsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("tramadditions")
                .then(Commands.literal("pair")
                    .executes(context -> {
                        String username = context.getSource().getTextName(); // This gets the username or display name
                        if(TramAdditions.userPinMap.containsKey(username)) {
                            context.getSource().sendSuccess(
                                    () -> Component.literal("Go to https://neebooo.is-a.dev/webui and enter the server pin of %s and the userpin %s!".formatted(TramAdditions.swuid,TramAdditions.userPinMap.get(username))), false);
                            return 1;
                        }
                        Random random = new Random();
                        String id = String.format("%04d", random.nextInt(10000));
                        TramAdditions.userPinMap.put(username,id);
                        context.getSource().sendSuccess(
                            () -> Component.literal("Go to https://neebooo.is-a.dev/webui and enter the server pin of %s and the userpin %s!".formatted(TramAdditions.swuid,id)), false);
                        return 1;
                    })
                )
        );
    }
}
