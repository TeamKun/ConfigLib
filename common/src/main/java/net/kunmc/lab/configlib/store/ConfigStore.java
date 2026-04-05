package net.kunmc.lab.configlib.store;

import net.kunmc.lab.configlib.CommonBaseConfig;
import net.kunmc.lab.configlib.migration.Migrations;

import java.io.Closeable;
import java.util.Timer;

public interface ConfigStore {
    boolean exists();

    CommonBaseConfig read(Class<? extends CommonBaseConfig> clazz, Migrations migrations);

    void write(CommonBaseConfig config);

    /**
     * Start watching for external changes to the config data.
     * The returned {@link Closeable} stops watching when closed.
     * {@code onChanged} is called when a change is detected.
     */
    Closeable startWatching(Timer timer, Runnable onChanged);
}
