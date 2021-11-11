package net.kunmc.lab.command;

import dev.kotx.flylib.command.Command;
import net.kunmc.lab.config.BaseConfig;

import java.util.List;
import java.util.function.Function;

enum SubCommand {
    Add("add", ConfigAddCommand::new, ConfigAddCommand::new),
    Remove("remove", ConfigRemoveCommand::new, ConfigRemoveCommand::new),
    Reload("reload", ConfigReloadCommand::new, ConfigReloadCommand::new),
    Clear("clear", ConfigClearCommand::new, ConfigClearCommand::new),
    Set("set", ConfigSetCommand::new, ConfigSetCommand::new),
    List("list", ConfigListCommand::new, ConfigListCommand::new);

    final String name;
    final Function<BaseConfig, Command> instantiator;
    final Function<List<BaseConfig>, Command> instantiator2;

    SubCommand(String name, Function<BaseConfig, Command> instantiator, Function<List<BaseConfig>, Command> instantiator2) {
        this.name = name;
        this.instantiator = instantiator;
        this.instantiator2 = instantiator2;
    }

    public Command of(BaseConfig config) {
        return instantiator.apply(config);
    }

    public Command of(List<BaseConfig> config) {
        return instantiator2.apply(config);
    }
}
