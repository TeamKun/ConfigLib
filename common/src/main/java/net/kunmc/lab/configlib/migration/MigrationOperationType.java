package net.kunmc.lab.configlib.migration;

public enum MigrationOperationType {
    RENAME("rename"),
    MOVE("move"),
    DELETE("delete"),
    SET("set"),
    DEFAULT_VALUE("defaultValue"),
    CONVERT("convert");

    private final String displayName;

    MigrationOperationType(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
