package net.kunmc.lab.configlib.store;

import java.util.Objects;

public final class AuditChange {
    private final String path;
    private final String beforeText;
    private final String afterText;

    public AuditChange(String path, String beforeText, String afterText) {
        this.path = Objects.requireNonNull(path, "path");
        this.beforeText = Objects.requireNonNull(beforeText, "beforeText");
        this.afterText = Objects.requireNonNull(afterText, "afterText");
    }

    public String path() {
        return path;
    }

    public String beforeText() {
        return beforeText;
    }

    public String afterText() {
        return afterText;
    }
}
