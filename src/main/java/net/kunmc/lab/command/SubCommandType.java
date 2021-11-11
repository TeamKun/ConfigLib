package net.kunmc.lab.command;

import dev.kotx.flylib.command.Command;
import net.kunmc.lab.config.BaseConfig;
import net.kunmc.lab.value.CollectionValue;
import net.kunmc.lab.value.SingleValue;
import net.kunmc.lab.value.Value;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

enum SubCommandType {
    Add("add",
            x -> x.getCollectionValues().stream().anyMatch(CollectionValue::addableByCommand),
            ConfigAddCommand::new,
            ConfigAddCommand::new),
    Remove("remove",
            x -> x.getCollectionValues().stream().anyMatch(CollectionValue::removableByCommand),
            ConfigRemoveCommand::new,
            ConfigRemoveCommand::new),
    Reload("reload",
            x -> !(x.getSingleValueFields().isEmpty() && x.getCollectionValueFields().isEmpty()),
            ConfigReloadCommand::new,
            ConfigReloadCommand::new),
    Clear("clear",
            x -> x.getCollectionValues().stream().anyMatch(CollectionValue::clearableByCommand),
            ConfigClearCommand::new,
            ConfigClearCommand::new),
    Set("set", x -> x.getSingleValues().stream().anyMatch(SingleValue::writableByCommand),
            ConfigSetCommand::new,
            ConfigSetCommand::new),
    List("list",
            x -> Stream.concat(x.getSingleValues().stream(), x.getCollectionValues().stream()).anyMatch(Value::listable),
            ConfigListCommand::new,
            ConfigListCommand::new);

    final String name;
    final Predicate<BaseConfig> hasEntryFor;
    final Function<BaseConfig, Command> instantiator;
    final Function<List<BaseConfig>, Command> instantiator2;

    SubCommandType(String name, Predicate<BaseConfig> hasEntryFor, Function<BaseConfig, Command> instantiator, Function<List<BaseConfig>, Command> instantiator2) {
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
