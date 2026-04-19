package net.kunmc.lab.configlib.schema;

import org.jetbrains.annotations.Nullable;

public final class ConfigSchemaMetadata {
    private final String description;

    public ConfigSchemaMetadata(@Nullable String description) {
        this.description = description;
    }

    @Nullable
    public String description() {
        return description;
    }

    public boolean hasDescription() {
        return description != null;
    }
}
