package hu.qliqs;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import hu.qliqs.commands.TramAdditionsCommand;

public class ModEvents {
    public static void init() {
        CommandRegistrationEvent.EVENT.register((dispatcher, context, selection) -> {
            TramAdditionsCommand.register(dispatcher);
        });
    }
}
