package net.kunmc.lab.command;

import dev.kotx.flylib.command.Command;
import net.kunmc.lab.config.BaseConfig;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

enum SubCommand {
    Add("add",
            x -> !x.getCollectionValueFields().isEmpty(),
            ConfigAddCommand::new,
            ConfigAddCommand::new),
    Remove("remove",
            x -> !x.getCollectionValueFields().isEmpty(),
            ConfigRemoveCommand::new,
            ConfigRemoveCommand::new),
    Reload("reload",
            x -> !(x.getSingleValueFields().isEmpty() && x.getCollectionValueFields().isEmpty()),
            ConfigReloadCommand::new,
            ConfigReloadCommand::new),
    Clear("clear",
            x -> !x.getCollectionValueFields().isEmpty(),
            ConfigClearCommand::new,
            ConfigClearCommand::new),
    Set("set", x -> !x.getSingleValueFields().isEmpty(),
            ConfigSetCommand::new,
            ConfigSetCommand::new),
    List("list",
            x -> !(x.getSingleValueFields().isEmpty() && x.getCollectionValueFields().isEmpty()),
            ConfigListCommand::new,
            ConfigListCommand::new);

    final String name;
    final Predicate<BaseConfig> hasEntryFor;
    final Function<BaseConfig, Command> instantiator;
    final Function<List<BaseConfig>, Command> instantiator2;

    SubCommand(String name, Predicate<BaseConfig> hasEntryFor, Function<BaseConfig, Command> instantiator, Function<List<BaseConfig>, Command> instantiator2) {
        this.name = name;
        this.hasEntryFor = hasEntryFor;
        this.instantiator = instantiator;
        this.instantiator2 = instantiator2;
    }

    public boolean hasEntryFor(BaseConfig config) {
        return hasEntryFor.test(config);
    }

    public boolean hasEntryFor(List<BaseConfig> configList) {
        return configList.stream()
                .anyMatch(this::hasEntryFor);
    }

    public Command of(BaseConfig config) {
        return instantiator.apply(config);
    }

    public Command of(List<BaseConfig> config) {
        return instantiator2.apply(config);
    }
}
