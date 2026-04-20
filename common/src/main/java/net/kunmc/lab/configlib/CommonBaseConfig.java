package net.kunmc.lab.configlib;

import com.google.common.base.Preconditions;
import com.google.gson.annotations.SerializedName;
import net.kunmc.lab.configlib.exception.ConfigValidationException;
import net.kunmc.lab.configlib.migration.MigrationContext;
import net.kunmc.lab.configlib.migration.Migrations;
import net.kunmc.lab.configlib.schema.ConfigSchema;
import net.kunmc.lab.configlib.schema.ConfigSchemaEntry;
import net.kunmc.lab.configlib.store.ConfigStore;
import net.kunmc.lab.configlib.store.HistoryEntry;
import net.kunmc.lab.configlib.util.ConfigUtil;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class CommonBaseConfig {
    @SuppressWarnings("unused")
    @SerializedName(ConfigKeys.VERSION)
    private int schemaVersion = 0;
    protected final transient Timer timer = new Timer();
    protected final transient Logger logger = Logger.getLogger(getClass().getName());
    private final transient List<Runnable> onChangeListeners = new CopyOnWriteArrayList<>();
    private final transient Object ioLock = new Object();
    private final transient ConfigModificationDetector modificationDetector = new ConfigModificationDetector(this);
    private transient boolean listEnabled = true;
    private transient boolean reloadEnabled = true;
    private transient boolean resetEnabled = true;
    private transient boolean historyEnabled = true;
    transient volatile boolean initialized = false;
    private transient String entryName;
    private transient Migrations migrations;
    private transient ConfigStore configStore;
    private transient Closeable configStoreWatcher;
    private transient ConfigSchema schema;

    protected CommonBaseConfig() {
        String s = getClass().getSimpleName();
        this.entryName = s.substring(0, 1)
                          .toLowerCase() + s.substring(1);
    }

    protected void entryName(@NotNull String entryName) {
        this.entryName = Objects.requireNonNull(entryName);
    }

    public final String entryName() {
        return entryName;
    }

    public final ConfigSchema schema() {
        return schema;
    }

    final void init(Option option) {
        migrations = new Migrations(option.migrations);
        schemaVersion = migrations.latestVersion();
        configStore = Objects.requireNonNull(createConfigStore());

        for (Value<?, ?> v : ConfigUtil.getValues(this)) {
            v.snapshotDefault();
        }

        schema = ConfigSchema.fromConfig(this);

        try {
            saveConfigIfAbsent();
            loadConfig();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        if (configStore.readHistory(getClass(), migrations)
                       .isEmpty()) {
            configStore.pushHistory(this);
        }

        modificationDetector.start(timer, option.modifyDetectionTimerPeriod);
        configStoreWatcher = configStore.startWatching(timer, () -> {
            try {
                loadConfig();
            } catch (ConfigValidationException ex) {
                logValidationFailure(ex);
            }
        }, option.fileWatchTimerPeriod);
    }

    public final void onChange(Runnable listener) {
        onChangeListeners.add(listener);
    }

    final void dispatchOnChange() {
        onChangeListeners.forEach(Runnable::run);
    }

    void detectModifications() {
        modificationDetector.detect();
    }

    final boolean isListEnabled() {
        return listEnabled;
    }

    final boolean isReloadEnabled() {
        return reloadEnabled;
    }

    final boolean isResetEnabled() {
        return resetEnabled;
    }

    final boolean isHistoryEnabled() {
        return historyEnabled;
    }

    protected final void disableList() {
        this.listEnabled = false;
    }

    protected final void disableReload() {
        this.reloadEnabled = false;
    }

    protected final void disableReset() {
        this.resetEnabled = false;
    }

    protected final void disableHistory() {
        this.historyEnabled = false;
    }

    /**
     * Creates the {@link ConfigStore} used for reading and writing this config.
     * Called once during {@link #init}.
     */
    protected abstract ConfigStore createConfigStore();

    protected void saveConfig() {
        withIoLock(this::saveConfigLocked);
    }

    protected void saveConfigIfAbsent() {
        withIoLock(() -> {
            if (!configStore.exists()) {
                saveConfigLocked();
            }
        });
    }

    protected void saveConfigIfPresent() {
        withIoLock(() -> {
            if (configStore.exists()) {
                saveConfigLocked();
            }
        });
    }

    void pushHistory() {
        withIoLock(() -> {
            configStore.pushHistory(this);
        });
    }

    /**
     * Runs a config mutation as one consistent change.
     * <p>
     * Use this when changing config values outside ConfigLib's built-in commands.
     * If the mutation changes any observed value after initialization, ConfigLib saves the
     * resulting config, appends one history entry, refreshes modification tracking, and
     * dispatches {@link #onChange(Runnable)} listeners.
     * </p>
     * <p>
     * Multiple value updates inside one mutation are treated as a single change. Read-only
     * operations should use {@link #inspect(Runnable)} or {@link #inspect(Supplier)} instead.
     * </p>
     *
     * @param mutation code that updates this config
     * @throws ConfigValidationException if the mutated config does not satisfy schema validation
     */
    public final void mutate(Runnable mutation) {
        withIoLock(() -> {
            mutation.run();
            if (!initialized || !modificationDetector.isModified()) {
                return;
            }

            saveConfigLocked();
            configStore.pushHistory(this);
            modificationDetector.initializeHash();
            dispatchOnChange();
        });
    }

    /**
     * Runs read-only code while holding this config's consistency lock.
     * <p>
     * Use this for inspecting multiple fields, collections, maps, history, or formatted values
     * that should be based on a single stable config state. This method does not save the config,
     * append history, or dispatch change listeners.
     * </p>
     *
     * @param inspector read-only code to run
     */
    public final void inspect(Runnable inspector) {
        inspect(() -> {
            inspector.run();
            return null;
        });
    }

    /**
     * Runs read-only code while holding this config's consistency lock and returns its result.
     * <p>
     * Use this for deriving a value from multiple config fields without allowing a concurrent
     * mutation to interleave with the calculation. This method does not save the config, append
     * history, or dispatch change listeners.
     * </p>
     *
     * @param inspector read-only code to run
     * @param <T>       result type
     * @return the value returned by {@code inspector}
     */
    public final <T> T inspect(Supplier<T> inspector) {
        return withIoLock(inspector);
    }

    private void saveConfigLocked() {
        validateConfig(this);
        CommonBaseConfig saved = configStore.write(this, getClass(), migrations);
        replaceWithValidatedConfig(saved);
    }

    void withIoLock(Runnable action) {
        withIoLock(() -> {
            action.run();
            return null;
        });
    }

    <T> T withIoLock(Supplier<T> action) {
        synchronized (ioLock) {
            return action.get();
        }
    }

    public boolean applyUndo(int stepsBack) {
        return withIoLock(() -> {
            if (!configStore.canUndo(stepsBack)) {
                return false;
            }
            CommonBaseConfig historical = configStore.undo(getClass(), migrations, stepsBack);
            ConfigUtil.replaceFields(this, historical, this);
            saveConfigLocked();
            modificationDetector.initializeHash();
            dispatchOnChange();
            return true;
        });
    }

    public List<HistoryEntry> readHistory() {
        return withIoLock(() -> {
            return configStore.readHistory(getClass(), migrations);
        });
    }

    /**
     * Loads this config from the backing store and applies it to the live config.
     *
     * @return true when stored config content was loaded; false when no store exists
     * @throws ConfigValidationException if the loaded config does not satisfy schema validation
     */
    protected final boolean loadConfig() {
        return withIoLock(() -> {
            if (!configStore.exists()) {
                return false;
            }

            CommonBaseConfig config = configStore.read(getClass(), migrations, this);
            validateConfig(config);

            ConfigUtil.replaceFields(this, config, this);
            if (!initialized) {
                ConfigUtil.getValues(this)
                          .stream()
                          .map(Value.class::cast)
                          .forEach(x -> x.dispatchInitialize(x.value()));
                modificationDetector.initializeHash();
                initialized = true;
                return true;
            }

            dispatchOnChange();
            return true;
        });
    }

    private void replaceWithValidatedConfig(CommonBaseConfig config) {
        validateConfig(config);
        ConfigUtil.replaceFields(this, config, this);
    }

    void validateCurrentConfig() {
        validateConfig(this);
    }

    void logValidationFailure(ConfigValidationException ex) {
        logger.log(Level.WARNING, ex.getMessage(), ex);
    }

    private void validateConfig(CommonBaseConfig config) {
        for (ConfigSchemaEntry<?> entry : schema.entries()) {
            Object value = entry.get(config);
            ConfigSchemaValidation.validate(entry, value);
        }
    }

    protected final void close() {
        timer.cancel();
        try {
            if (configStoreWatcher != null) {
                configStoreWatcher.close();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static class Option {
        int modifyDetectionTimerPeriod = 100;
        int fileWatchTimerPeriod = 100;
        final TreeMap<Integer, Consumer<MigrationContext>> migrations = new TreeMap<>();

        public Option() {
        }

        public Option modifyDetectionTimerPeriod(int period) {
            Preconditions.checkArgument(period > 0);
            this.modifyDetectionTimerPeriod = period;
            return this;
        }

        public Option fileWatchTimerPeriod(int period) {
            Preconditions.checkArgument(period > 0);
            this.fileWatchTimerPeriod = period;
            return this;
        }

        public Option migration(int version, Consumer<MigrationContext> migration) {
            Preconditions.checkArgument(version > 0, "version must be positive");
            migrations.put(version, Objects.requireNonNull(migration));
            return this;
        }
    }

}
