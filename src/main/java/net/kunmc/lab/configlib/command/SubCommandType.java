package net.kunmc.lab.configlib.command;

import dev.kotx.flylib.command.Command;
import net.kunmc.lab.configlib.config.BaseConfig;
import net.kunmc.lab.configlib.value.CollectionValue;
import net.kunmc.lab.configlib.value.SingleValue;
import net.kunmc.lab.configlib.value.Value;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum SubCommandType {
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

    public final String name;
    private final Predicate<BaseConfig> hasEntryFor;
    private final Function<BaseConfig, Command> instantiator;
    private final Function<Set<BaseConfig>, Command> instantiator2;

    SubCommandType(String name, Predicate<BaseConfig> hasEntryFor, Function<BaseConfig, Command> instantiator, Function<Set<BaseConfig>, Command> instantiator2) {
        this.name = name;
        this.hasEntryFor = hasEntryFor;
        this.instantiator = instantiator;
        this.instantiator2 = instantiator2;
    }

    public boolean hasEntryFor(BaseConfig config) {
        return hasEntryFor.test(config);
    }

    public Map<BaseConfig, Boolean> hasEntryFor(Set<BaseConfig> configSet) {
        return configSet.stream()
                .collect(Collectors.toMap(baseConfig -> baseConfig, this::hasEntryFor));
    }

    public Command of(BaseConfig config) {
        return instantiator.apply(config);
    }

    public Command of(Set<BaseConfig> config) {
        return instantiator2.apply(config);
    }
}
