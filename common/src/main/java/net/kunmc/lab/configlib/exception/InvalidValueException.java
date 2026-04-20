package net.kunmc.lab.configlib.exception;

import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.util.ListUtil;

import java.util.List;
import java.util.function.Consumer;

public class InvalidValueException extends Exception {
    private final Consumer<CommandContext> messageSender;

    public InvalidValueException() {
        this("不正な値です");
    }

    public InvalidValueException(String message, String... messages) {
        this(ListUtil.asList(message, messages),
             String.join(System.lineSeparator(), ListUtil.asList(message, messages)));
    }

    public InvalidValueException(List<String> messages) {
        this(messages, String.join(System.lineSeparator(), messages));
    }

    private InvalidValueException(List<String> messages, String logMessage) {
        super(logMessage);
        this.messageSender = ctx -> {
            messages.forEach(ctx::sendFailure);
        };
    }

    public InvalidValueException(Consumer<CommandContext> messageSender) {
        this("Custom validation message", messageSender);
    }

    public InvalidValueException(String logMessage, Consumer<CommandContext> messageSender) {
        super(logMessage);
        this.messageSender = messageSender;
    }

    public void sendMessage(CommandContext ctx) {
        this.messageSender.accept(ctx);
    }
}
