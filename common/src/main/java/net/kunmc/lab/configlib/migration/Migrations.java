package net.kunmc.lab.configlib.migration;

import java.util.TreeMap;
import java.util.function.Consumer;

public class Migrations {
    private final TreeMap<Integer, Consumer<MigrationContext>> migrations;

    public Migrations(TreeMap<Integer, Consumer<MigrationContext>> migrations) {
        this.migrations = new TreeMap<>(migrations);
    }

    public int latestVersion() {
        return migrations.isEmpty() ? 0 : migrations.lastKey();
    }

    public boolean apply(int storedVersion, MigrationContext ctx) {
        if (migrations.isEmpty()) {
            return false;
        }

        int latestVersion = migrations.lastKey();
        if (storedVersion >= latestVersion) {
            return false;
        }

        migrations.entrySet()
                  .stream()
                  .filter(e -> e.getKey() > storedVersion)
                  .forEach(e -> e.getValue()
                                 .accept(ctx));

        return true;
    }
}
