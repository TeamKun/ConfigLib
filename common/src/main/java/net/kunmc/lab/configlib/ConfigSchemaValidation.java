package net.kunmc.lab.configlib;

import net.kunmc.lab.configlib.exception.ConfigValidationException;
import net.kunmc.lab.configlib.exception.InvalidValueException;
import net.kunmc.lab.configlib.schema.ConfigSchemaEntry;

final class ConfigSchemaValidation {
    private ConfigSchemaValidation() {
    }

    @SuppressWarnings("unchecked")
    static void validate(ConfigSchemaEntry<?> entry, Object value) {
        try {
            ((ConfigSchemaEntry<Object>) entry).validate(value);
        } catch (InvalidValueException e) {
            throw new ConfigValidationException(entry.path(), value, e);
        }
    }
}
