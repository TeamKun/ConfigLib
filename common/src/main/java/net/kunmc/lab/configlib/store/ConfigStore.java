package net.kunmc.lab.configlib.store;

import net.kunmc.lab.configlib.CommonBaseConfig;
import net.kunmc.lab.configlib.migration.Migrations;

import java.io.Closeable;
import java.util.List;
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
    Closeable startWatching(Timer timer, Runnable onChanged, int periodMs);

    /**
     * Save the current config state to history.
     */
    void pushHistory(CommonBaseConfig config);

    /**
     * Returns true if there are at least {@code stepsBack + 1} history entries.
     */
    boolean canUndo(int stepsBack);

    /**
     * Discards the top {@code stepsBack} entries and returns the new top as a deserialized config.
     * Only call when {@link #canUndo(int)} is true.
     */
    CommonBaseConfig undo(Class<? extends CommonBaseConfig> clazz, Migrations migrations, int stepsBack);

    /**
     * Returns all history entries, most recent first.
     */
    List<HistoryEntry> readHistory(Class<? extends CommonBaseConfig> clazz, Migrations migrations);
}
