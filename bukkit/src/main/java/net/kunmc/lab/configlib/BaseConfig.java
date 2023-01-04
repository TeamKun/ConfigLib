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
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.TimerTask;

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

    public BaseConfig(@NotNull Plugin plugin) {
        this(plugin, true);
    }

    public BaseConfig(@NotNull Plugin plugin, boolean makeConfigFile) {
        this.plugin = plugin;
        this.makeConfigFile = makeConfigFile;

        init();

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
                                      close();
                                  }
                              }
                          }, plugin);

                    cancel();
                }
            }
        }, 100, 100);
    }

    public Plugin plugin() {
        return plugin;
    }

    @Override
    protected Gson gson() {
        return gson;
    }

    @Override
    File getConfigFolder() {
        return plugin.getDataFolder();
    }
}
