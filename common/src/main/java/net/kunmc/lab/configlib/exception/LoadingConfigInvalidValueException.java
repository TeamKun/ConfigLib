package net.kunmc.lab.configlib.exception;

import net.kunmc.lab.configlib.schema.ConfigSchemaPath;

public final class LoadingConfigInvalidValueException extends RuntimeException {
    private final ConfigValidationException validationException;

    public LoadingConfigInvalidValueException(ConfigValidationException validationException) {
        super(validationException);
        this.validationException = validationException;
    }

    public ConfigSchemaPath path() {
        return validationException.path();
    }

    public Object value() {
        return validationException.value();
    }

    public ConfigValidationException validationException() {
        return validationException;
    }
}
