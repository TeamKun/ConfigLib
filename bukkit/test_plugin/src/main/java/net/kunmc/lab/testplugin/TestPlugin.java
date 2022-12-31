package net.kunmc.lab.testplugin;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandLib;
import net.kunmc.lab.configlib.ConfigCommand;
import net.kunmc.lab.configlib.ConfigCommandBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class TestPlugin extends JavaPlugin {
    public static void print(Object obj) {
        if (Objects.equals(System.getProperty("plugin.env"), "DEV")) {
            System.out.printf("[%s] %s%n", TestPlugin.class.getSimpleName(), obj);
        }
    }

    public static void broadcast(Object obj) {
        if (Objects.equals(System.getProperty("plugin.env"), "DEV")) {
            Bukkit.broadcastMessage(String.format("[%s] %s", TestPlugin.class.getSimpleName(), obj));
        }
    }

    @Override
    public void onEnable() {
        Config config = new Config(this);
        ConfigCommand configCommand = new ConfigCommandBuilder(config).build();

        CommandLib.register(this, new Command("configlibtest") {{
            addChildren(configCommand);
        }});
    }

    @Override
    public void onDisable() {
    }
}
