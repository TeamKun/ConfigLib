package net.kunmc.lab.configlib;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kunmc.lab.commandlib.Nameable;
import net.kunmc.lab.configlib.gson.*;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.codehaus.plexus.util.ReflectionUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public abstract class BaseConfig implements Listener {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting()
                                                      .enableComplexMapKeySerialization()
                                                      .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC)
                                                      .registerTypeAdapter(Pair.class, new PairTypeAdapter<>())
                                                      .registerTypeHierarchyAdapter(Team.class, new TeamTypeAdapter())
                                                      .registerTypeHierarchyAdapter(BlockData.class,
                                                                                    new BlockDataTypeAdapter())
                                                      .registerTypeHierarchyAdapter(ItemStack.class,
                                                                                    new ItemStackTypeAdapter())
                                                      .registerTypeHierarchyAdapter(Location.class,
                                                                                    new LocationTypeAdapter())
                                                      .registerTypeHierarchyAdapter(Value.class, new ValueTypeAdapter())
                                                      .registerTypeHierarchyAdapter(Nameable.class,
                                                                                    new NameableTypeAdapter())
                                                      .registerTypeHierarchyAdapter(Set.class, new SetTypeAdapter())
                                                      .create();
    private final transient Plugin plugin;
    private final transient List<Runnable> onInitializeListeners = new ArrayList<>();
    protected transient boolean enableGet = true;
    protected transient boolean enableList = true;
    protected transient boolean enableModify = true;
    protected transient boolean enableReload = true;
    private transient String entryName = "";

    public BaseConfig(@NotNull Plugin plugin) {
        this(plugin, true);
    }

    public BaseConfig(@NotNull Plugin plugin, boolean makeConfigFile) {
        this.plugin = plugin;

        if (!makeConfigFile) {
            return;
        }
        plugin.getDataFolder()
              .mkdir();

        // コンストラクタの処理内でシリアライズするとフィールドの初期化が終わってない状態でシリアライズされるため遅延させている.
        new BukkitRunnable() {
            @Override
            public void run() {
                saveConfigIfAbsent();
                loadConfig();
                onInitializeListeners.forEach(Runnable::run);
            }
        }.runTask(plugin);

        Timer timer = new Timer();
        WatchService watcher;
        WatchKey watchKey;
        try {
            watcher = FileSystems.getDefault()
                                 .newWatchService();
            watchKey = plugin.getDataFolder()
                             .toPath()
                             .register(watcher, ENTRY_MODIFY);

            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    for (WatchEvent<?> e : watchKey.pollEvents()) {
                        Path filePath = plugin.getDataFolder()
                                              .toPath()
                                              .resolve((Path) e.context());
                        if (filePath.equals(getConfigFile().toPath())) {
                            loadConfig();
                        }
                    }

                    watchKey.reset();
                }
            }, 0, 500);
        } catch (IOException e) {
            e.printStackTrace();
            throw new UncheckedIOException(e);
        }

        // Pluginがenabledになっていない状態でregisterすると例外が発生するため遅延,ループさせている
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (plugin.isEnabled()) {
                    Bukkit.getPluginManager()
                          .registerEvents(new Listener() {
                              @EventHandler
                              public void onPluginDisable(PluginDisableEvent e) {
                                  if (e.getPlugin() == plugin) {
                                      try {
                                          timer.cancel();
                                          watcher.close();
                                          watchKey.cancel();
                                      } catch (IOException ex) {
                                          ex.printStackTrace();
                                      }
                                  }
                              }
                          }, plugin);

                    cancel();
                }
            }
        }, 100, 100);
    }

    public static <T extends BaseConfig> T newInstanceFrom(@NotNull File configJSON,
                                                           @NotNull Constructor<T> constructor,
                                                           Object... arguments) {
        String filename = configJSON.getName();
        String json = readJson(configJSON);
        Class<T> clazz = constructor.getDeclaringClass();

        try {
            T config = constructor.newInstance(arguments);
            config.setEntryName(filename.substring(0, filename.lastIndexOf('.')));
            replaceFields(clazz, gson.fromJson(json, clazz), config);
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
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(jsonFile),
                                                                StandardCharsets.UTF_8)) {
            writer.write(json);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void replaceFields(Class<?> clazz, Object src, Object dst) {
        for (Field field : ReflectionUtils.getFieldsIncludingSuperclasses(clazz)) {
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
            List<Field> fieldList = ReflectionUtils.getFieldsIncludingSuperclasses(field.getType());
            Object srcObj = field.get(src);
            Object dstObj = field.get(dst);

            if (fieldList.isEmpty()) {
                field.set(dst, srcObj);
            } else {
                replaceFields(field.getType(), srcObj, dstObj);
            }
        } catch (NullPointerException ignored) {
            // 新しいフィールドが追加されるとNullPointerExceptionが発生するため握りつぶしている
        }
    }

    /**
     * set listener fired on initialization.
     */
    protected final void onInitialize(Runnable onLoad) {
        onInitializeListeners.add(onLoad);
    }

    boolean isGetEnabled() {
        return enableGet;
    }

    boolean isListEnabled() {
        return enableList;
    }

    boolean isModifyEnabled() {
        return enableModify;
    }

    boolean isReloadEnabled() {
        return enableReload;
    }

    protected void setEntryName(@NotNull String entryName) {
        this.entryName = entryName;
    }

    public Plugin plugin() {
        return plugin;
    }

    public File getConfigFile() {
        return new File(plugin.getDataFolder(), entryName() + ".json");
    }

    public String entryName() {
        if (entryName.equals("")) {
            String n = getClass().getSimpleName();
            return n.substring(0, 1)
                    .toLowerCase() + n.substring(1);
        } else {
            return entryName;
        }
    }

    protected void saveConfig() {
        try {
            getConfigFile().createNewFile();
            writeJson(getConfigFile(), gson.toJson(this));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
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

    protected boolean loadConfig() {
        if (!getConfigFile().exists()) {
            return false;
        }

        BaseConfig config = gson.fromJson(readJson(getConfigFile()), this.getClass());
        replaceFields(this.getClass(), config, this);
        return true;
    }
}
