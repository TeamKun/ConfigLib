package net.kunmc.lab.configlib.exception;

import java.lang.reflect.Field;

public final class LoadingConfigInvalidValueException extends RuntimeException {
    private final Field valueField;

    public LoadingConfigInvalidValueException(Field valueField, InvalidValueException e) {
        super(e);
        this.valueField = valueField;
    }

    public Field getValueField() {
        return valueField;
    }
}
