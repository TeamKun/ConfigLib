package net.kunmc.lab.configlib;

import com.google.common.base.Preconditions;
import com.google.gson.annotations.SerializedName;
import net.kunmc.lab.configlib.exception.InvalidValueException;
import net.kunmc.lab.configlib.exception.LoadingConfigInvalidValueException;
import net.kunmc.lab.configlib.migration.MigrationContext;
import net.kunmc.lab.configlib.migration.Migrations;
import net.kunmc.lab.configlib.store.ConfigStore;
import net.kunmc.lab.configlib.store.HistoryEntry;
import net.kunmc.lab.configlib.util.ConfigUtil;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class CommonBaseConfig {
    @SuppressWarnings("unused")
    @SerializedName("_version_")
    private int schemaVersion = 0;
    protected final transient Timer timer = new Timer();
    protected final transient Logger logger = Logger.getLogger(getClass().getName());
    private final transient List<Runnable> onReloadListeners = new CopyOnWriteArrayList<>();
    final transient Object ioLock = new Object();
    private final transient ConfigModificationDetector modificationDetector = new ConfigModificationDetector(this);
    protected transient boolean enableList = true;
    protected transient boolean enableReload = true;
    protected transient boolean enableReset = true;
    protected transient boolean enableHistory = true;
    transient volatile boolean initialized = false;
    private transient String entryName;
    private transient Migrations migrations;
    private transient ConfigStore configStore;
    private transient Closeable configStoreWatcher;

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

    final void init(Option option) {
        migrations = new Migrations(option.migrations);
        schemaVersion = migrations.latestVersion();
        configStore = createConfigStore();

        for (Value<?, ?> v : ConfigUtil.getValues(this)) {
            v.snapshotDefault();
        }

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
            } catch (LoadingConfigInvalidValueException ex) {
                logger.log(Level.WARNING,
                           String.format("\"%s\"'s validation failed.",
                                         ex.getValueField()
                                           .getName()),
                           ex);
            }
        });
    }

    public final void onReload(Runnable onReload) {
        onReloadListeners.add(onReload);
    }

    final boolean isListEnabled() {
        return enableList;
    }

    final boolean isReloadEnabled() {
        return enableReload;
    }

    final boolean isResetEnabled() {
        return enableReset;
    }

    final boolean isHistoryEnabled() {
        return enableHistory;
    }

    /**
     * Creates the {@link ConfigStore} used for reading and writing this config.
     * Called once during {@link #init}.
     */
    protected abstract ConfigStore createConfigStore();

    protected void saveConfig() {
        synchronized (ioLock) {
            configStore.write(this);
        }
    }

    protected void saveConfigIfAbsent() {
        if (!configStore.exists()) {
            saveConfig();
        }
    }

    protected void saveConfigIfPresent() {
        if (configStore.exists()) {
            saveConfig();
        }
    }

    void pushHistory() {
        synchronized (ioLock) {
            configStore.pushHistory(this);
        }
    }

    public boolean applyUndo(int stepsBack) {
        synchronized (ioLock) {
            if (!configStore.canUndo(stepsBack)) {
                return false;
            }
            CommonBaseConfig historical = configStore.undo(getClass(), migrations, stepsBack);
            ConfigUtil.replaceFields(getClass(), historical, this);
            configStore.write(this);
            modificationDetector.initializeHash();
            onReloadListeners.forEach(Runnable::run);
            return true;
        }
    }

    public List<HistoryEntry> readHistory() {
        synchronized (ioLock) {
            return configStore.readHistory(getClass(), migrations);
        }
    }

    protected final boolean loadConfig() throws LoadingConfigInvalidValueException {
        synchronized (ioLock) {
            if (!configStore.exists()) {
                return false;
            }

            CommonBaseConfig config = configStore.read(getClass(), migrations);

            for (Field field : ConfigUtil.getValueFields(this)) {
                try {
                    Value<?, ?> loaded = ((Value<?, ?>) field.get(config));
                    if (loaded == null || loaded.value() == null) {
                        continue;
                    }

                    Value current = ((Value<?, ?>) field.get(this));
                    current.validate(loaded.value());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvalidValueException e) {
                    throw new LoadingConfigInvalidValueException(field, e);
                }
            }

            ConfigUtil.replaceFields(getClass(), config, this);
            if (!initialized) {
                ConfigUtil.getValues(this)
                          .stream()
                          .map(Value.class::cast)
                          .forEach(x -> x.onInitializeValue(x.value()));
                modificationDetector.initializeHash();
                initialized = true;
                return true;
            }

            onReloadListeners.forEach(Runnable::run);
            return true;
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
        int modifyDetectionTimerPeriod = 500;
        final TreeMap<Integer, Consumer<MigrationContext>> migrations = new TreeMap<>();

        public Option() {
        }

        public Option modifyDetectionTimerPeriod(int period) {
            Preconditions.checkArgument(period > 0);
            this.modifyDetectionTimerPeriod = period;
            return this;
        }

        public Option migration(int version, Consumer<MigrationContext> migration) {
            Preconditions.checkArgument(version > 0, "version must be positive");
            migrations.put(version, Objects.requireNonNull(migration));
            return this;
        }
    }
}
