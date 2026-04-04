package net.kunmc.lab.configlib;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import net.kunmc.lab.configlib.exception.InvalidValueException;
import net.kunmc.lab.configlib.exception.LoadingConfigInvalidValueException;
import net.kunmc.lab.configlib.util.ConfigUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.logging.Logger;

public abstract class CommonBaseConfig {
    protected final transient Timer timer = new Timer();
    protected final transient Logger logger = Logger.getLogger(getClass().getName());
    private final transient List<Runnable> onReloadListeners = new CopyOnWriteArrayList<>();
    private final transient Object lock = new Object();
    private final transient ConfigModificationDetector modificationDetector = new ConfigModificationDetector(this);
    private final transient ConfigFileWatcher fileWatcher = new ConfigFileWatcher(this);
    protected transient boolean enableList = true;
    protected transient boolean enableReload = true;
    transient volatile boolean initialized = false;
    private transient String entryName;

    protected CommonBaseConfig() {
        String s = getClass().getSimpleName();
        this.entryName = s.substring(0, 1)
                          .toLowerCase() + s.substring(1);
    }

    public static <T extends CommonBaseConfig> T newInstanceFrom(@NotNull File jsonFile,
                                                                 @NotNull Constructor<T> constructor,
                                                                 Object... arguments) {
        String filename = jsonFile.getName();
        String json = ConfigFileIO.readJson(jsonFile);
        Class<T> clazz = constructor.getDeclaringClass();

        try {
            T config = constructor.newInstance(arguments);
            config.entryName(filename.substring(0, filename.lastIndexOf('.')));
            ConfigUtil.replaceFields(clazz,
                                     config.gson()
                                           .fromJson(json, clazz),
                                     config);
            return config;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    protected void entryName(@NotNull String entryName) {
        this.entryName = Objects.requireNonNull(entryName);
    }

    public final String entryName() {
        return entryName;
    }

    final void init(Consumer<Option> options) {
        Option option = new Option();
        options.accept(option);
        getConfigFolder().mkdirs();

        try {
            saveConfigIfAbsent();
            loadConfig();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        modificationDetector.start(timer, option.modifyDetectionTimerPeriod);
        fileWatcher.start(timer, option.jsonParseExceptionHandler);
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

    protected abstract Gson gson();

    protected abstract File getConfigFolder();

    public final File getConfigFile() {
        return new File(getConfigFolder(), entryName() + ".json");
    }

    protected void saveConfig() {
        synchronized (lock) {
            try {
                getConfigFile().createNewFile();
                ConfigFileIO.writeJson(getConfigFile(), gson().toJson(this));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    protected void saveConfigIfAbsent() {
        if (!getConfigFile().exists()) {
            saveConfig();
        }
    }

    protected void saveConfigIfPresent() {
        if (getConfigFile().exists()) {
            saveConfig();
        }
    }

    protected final boolean loadConfig() throws LoadingConfigInvalidValueException {
        synchronized (lock) {
            if (!getConfigFile().exists()) {
                return false;
            }

            CommonBaseConfig config = gson().fromJson(ConfigFileIO.readJson(getConfigFile()), getClass());

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
        fileWatcher.close();
    }

    public static final class Option {
        int modifyDetectionTimerPeriod = 500;
        Consumer<Exception> jsonParseExceptionHandler = Throwable::printStackTrace;

        Option() {
        }

        public Option modifyDetectionTimerPeriod(int period) {
            Preconditions.checkArgument(period > 0);
            this.modifyDetectionTimerPeriod = period;
            return this;
        }

        public Option jsonParseExceptionHandler(Consumer<Exception> handler) {
            this.jsonParseExceptionHandler = Objects.requireNonNull(handler);
            return this;
        }
    }
}
