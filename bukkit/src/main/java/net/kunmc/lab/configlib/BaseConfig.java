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
import java.util.function.Consumer;

public abstract class BaseConfig extends CommonBaseConfig implements Listener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
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
    private final transient Consumer<Option> options;

    public BaseConfig(@NotNull Plugin plugin) {
        this(plugin, option -> {
        });
    }

    public BaseConfig(@NotNull Plugin plugin, Consumer<Option> options) {
        this.plugin = plugin;
        this.options = options;
    }

    /**
     * Starts the automatic config synchronization. Must be called at the end of the subclass constructor.
     * The plugin must already be enabled (i.e., called from {@code onEnable}).
     */
    public BaseConfig initialize() {
        Bukkit.getPluginManager()
              .registerEvents(new Listener() {
                  @EventHandler
                  public void onPluginDisable(PluginDisableEvent e) {
                      if (e.getPlugin() == plugin) {
                          close();
                      }
                  }
              }, plugin);
        init(options);
        return this;
    }

    public Plugin plugin() {
        return plugin;
    }

    @Override
    protected Gson gson() {
        return GSON;
    }

    @Override
    protected File getConfigFolder() {
        return plugin.getDataFolder();
    }
}
