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
                        Random random = new Random();
                        String id = String.format("%04d", random.nextInt(10000));
                        String username = context.getSource().getTextName(); // This gets the username or display name
                        TramAdditions.userPinMap.put(username,id);
                        context.getSource().sendSuccess(
                            () -> Component.literal("Go to https://neebooo.is-a.dev/futar and enter the server pin of %s and the userpin %s!".formatted(TramAdditions.swuid,id)), false);
                        return 1;
                    })
                )
        );
    }
}
