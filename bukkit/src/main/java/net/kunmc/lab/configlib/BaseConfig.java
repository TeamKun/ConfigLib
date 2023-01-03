package net.kunmc.lab.configlib;

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
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Modifier;
import java.nio.file.*;
import java.util.*;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public abstract class BaseConfig extends CommonBaseConfig implements Listener {
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

    /**
     * set listener fired on initialization.
     */
    protected final void onInitialize(Runnable onLoad) {
        onInitializeListeners.add(onLoad);
    }

    public Plugin plugin() {
        return plugin;
    }

    @Override
    protected Gson gson() {
        return gson;
    }

    @Override
    public File getConfigFile() {
        return new File(plugin.getDataFolder(), entryName() + ".json");
    }
}
