package net.kunmc.lab.configlib;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import com.google.gson.Gson;
import net.kunmc.lab.configlib.exception.InvalidValueException;
import net.kunmc.lab.configlib.exception.LoadingConfigInvalidValueException;
import net.kunmc.lab.configlib.util.ConfigUtil;
import net.kunmc.lab.configlib.util.ReflectionUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public abstract class CommonBaseConfig {
    protected final transient Timer timer = new Timer();
    protected final transient Logger logger = Logger.getLogger(getClass().getName());
    private final transient List<Runnable> onReloadListeners = new ArrayList<>();
    private final transient Object lock = new Object();
    private final transient Map<Field, Pair<Object, Integer>> fieldToObjectAndHashMap = new HashMap<>();
    protected transient boolean enableList = true;
    protected transient boolean enableReload = true;
    private transient volatile boolean initialized = false;
    private transient String entryName;
    private transient WatchService watchService;
    private transient WatchKey watchKey;

    protected CommonBaseConfig() {
        String s = getClass().getSimpleName();
        this.entryName = s.substring(0, 1)
                          .toLowerCase() + s.substring(1);
    }

    public static <T extends CommonBaseConfig> T newInstanceFrom(@NotNull File jsonFile,
                                                                 @NotNull Constructor<T> constructor,
                                                                 Object... arguments) {
        String filename = jsonFile.getName();
        String json = readJson(jsonFile);
        Class<T> clazz = constructor.getDeclaringClass();

        try {
            T config = constructor.newInstance(arguments);
            config.setEntryName(filename.substring(0, filename.lastIndexOf('.')));
            replaceFields(clazz,
                          config.gson()
                                .fromJson(json, clazz),
                          config);
            return config;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static String readJson(File jsonFile) {
        try {
            return Files.readLines(jsonFile, StandardCharsets.UTF_8)
                        .stream()
                        .collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void writeJson(File jsonFile, String json) {
        try (OutputStreamWriter writer = new OutputStreamWriter(java.nio.file.Files.newOutputStream(jsonFile.toPath()),
                                                                StandardCharsets.UTF_8)) {
            writer.write(json);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void replaceFields(Class<?> clazz, Object src, Object dst) {
        for (Field field : ReflectionUtil.getFieldsIncludingSuperclasses(clazz)) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                continue;
            }

            try {
                field.setAccessible(true);
            } catch (Exception e) {
                // InaccessibleObjectExceptionが発生した場合スルーする
                continue;
            }

            try {
                replaceField(field, src, dst);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void replaceField(Field field, Object src, Object dst) throws IllegalAccessException {
        try {
            List<Field> fieldList = ReflectionUtil.getFieldsIncludingSuperclasses(field.getType());
            Object srcObj = field.get(src);
            Object dstObj = field.get(dst);

            // jsonにキーが存在しない場合nullになる
            if (srcObj == null) {
                return;
            }

            if (fieldList.isEmpty()) {
                field.set(dst, srcObj);
            } else {
                replaceFields(field.getType(), srcObj, dstObj);
            }
        } catch (NullPointerException ignored) {
            // 新しいフィールドが追加されるとNullPointerExceptionが発生するため握りつぶしている
        }
    }

    protected void setEntryName(@NotNull String entryName) {
        this.entryName = entryName;
    }

    public final String entryName() {
        return entryName;
    }

    final void init(Consumer<Option> options) {
        Option option = new Option();
        options.accept(option);

        getConfigFolder().mkdirs();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!initialized) {
                    return;
                }

                boolean modified = false;
                for (Map.Entry<Field, Pair<Object, Integer>> entry : fieldToObjectAndHashMap.entrySet()) {
                    Field field = entry.getKey();
                    Object o = entry.getValue()
                                    .getKey();
                    int oldHash = entry.getValue()
                                       .getValue();
                    if (o instanceof Value) {
                        Value value = ((Value) o);
                        int newHash = value.valueHashCode();
                        if (newHash != oldHash) {
                            value.onModifyValue(value.value());
                            fieldToObjectAndHashMap.put(field, Pair.of(value, newHash));
                            modified = true;
                        }
                    } else {
                        try {
                            // 通常のクラスだとインスタンスが変わっている可能性があるため再取得
                            Object newObj = field.get(CommonBaseConfig.this);
                            int newHash = Objects.hashCode(newObj);
                            if (newHash != oldHash) {
                                fieldToObjectAndHashMap.put(field, Pair.of(newObj, newHash));
                                modified = true;
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (modified) {
                    saveConfigIfPresent();
                }
            }
        }, 0, option.modifyDetectionTimerPeriod);

        try {
            saveConfigIfAbsent();
            loadConfig();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        try {
            watchService = FileSystems.getDefault()
                                      .newWatchService();
            watchKey = getConfigFolder().toPath()
                                        .register(watchService, ENTRY_MODIFY);
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    for (WatchEvent<?> e : watchKey.pollEvents()) {
                        Path filePath = getConfigFolder().toPath()
                                                         .resolve((Path) e.context());
                        if (filePath.equals(getConfigFile().toPath())) {
                            try {
                                loadConfig();
                            } catch (LoadingConfigInvalidValueException ex) {
                                logger.log(Level.WARNING,
                                           String.format("\"%s\"'s validation failed.",
                                                         ex.getValueField()
                                                           .getName()),
                                           ex);
                            } catch (Exception ex) {
                                option.jsonParseExceptionHandler.accept(ex);
                            }
                        }
                    }
                    watchKey.reset();
                }
            }, 0, 500);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
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
                writeJson(getConfigFile(), gson().toJson(this));
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

            CommonBaseConfig config = gson().fromJson(readJson(getConfigFile()), getClass());

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

            replaceFields(getClass(), config, this);
            if (!initialized) {
                ConfigUtil.getValues(this)
                          .stream()
                          .map(Value.class::cast)
                          .forEach(x -> x.onInitializeValue(x.value()));
                initializeHash();
                initialized = true;
                return true;
            }

            onReloadListeners.forEach(Runnable::run);
            return true;
        }
    }

    private void initializeHash() {
        ConfigUtil.getObservedFields(this, Object.class)
                  .forEach(x -> {
                      try {
                          Object o = x.get(this);
                          if (o instanceof Value<?, ?>) {
                              Value<?, ?> value = ((Value<?, ?>) o);
                              fieldToObjectAndHashMap.put(x, Pair.of(value, value.valueHashCode()));
                          } else {
                              fieldToObjectAndHashMap.put(x, Pair.of(o, Objects.hashCode(o)));
                          }
                      } catch (IllegalAccessException e) {
                          throw new RuntimeException(e);
                      }
                  });
    }

    protected final void close() {
        try {
            timer.cancel();
            if (watchService != null) {
                watchService.close();
            }
            if (watchKey != null) {
                watchKey.cancel();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
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
