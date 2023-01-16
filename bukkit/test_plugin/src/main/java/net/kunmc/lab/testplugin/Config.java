package net.kunmc.lab.testplugin;

import net.kunmc.lab.configlib.BaseConfig;
import net.kunmc.lab.configlib.value.UUIDValue;
import net.kunmc.lab.configlib.value.collection.UUIDSetValue;
import net.kunmc.lab.configlib.value.map.UUID2LocationMapValue;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Config extends BaseConfig {
    public final UUIDValue uuidValue = new UUIDValue().description("test description");
    public final UUIDSetValue uuidSetValue = new UUIDSetValue().onModify(TestPlugin::broadcast);
    public final UUID2LocationMapValue uuid2LocationMapValue = new UUID2LocationMapValue();
    public final List<String> strings = new ArrayList<>();

    public Config(@NotNull Plugin plugin) {
        super(plugin);

        strings.add("hogehoge");

        onInitialize(() -> {
            Bukkit.broadcastMessage("config initialize");
        });
    }
}
