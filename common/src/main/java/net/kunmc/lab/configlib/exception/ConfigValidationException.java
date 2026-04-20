package net.kunmc.lab.configlib.exception;

import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.schema.ConfigSchemaPath;

import java.util.Objects;

public final class ConfigValidationException extends Exception {
    private final ConfigSchemaPath path;
    private final Object value;
    private final InvalidValueException validationCause;

    public ConfigValidationException(ConfigSchemaPath path, Object value, InvalidValueException validationCause) {
        super(validationCause);
        this.path = Objects.requireNonNull(path, "path");
        this.value = value;
        this.validationCause = Objects.requireNonNull(validationCause, "validationCause");
    }

    public ConfigSchemaPath path() {
        return path;
    }

    public Object value() {
        return value;
    }

    public InvalidValueException validationCause() {
        return validationCause;
    }

    public void sendMessage(CommandContext ctx) {
        ctx.sendFailure("Validation failed for " + path.asString() + " (value: " + String.valueOf(value) + ").");
        validationCause.sendMessage(ctx);
    }
}
