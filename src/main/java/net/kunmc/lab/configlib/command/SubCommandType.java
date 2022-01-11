package net.kunmc.lab.configlib.command;

import dev.kotx.flylib.command.Command;
import net.kunmc.lab.configlib.config.BaseConfig;
import net.kunmc.lab.configlib.util.ConfigUtil;
import net.kunmc.lab.configlib.value.SingleValue;
import net.kunmc.lab.configlib.value.Value;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

enum SubCommandType {
    Reload("reload",
            BaseConfig::isEnableReload,
            x -> !(ConfigUtil.getSingleValueFields(x).isEmpty() && ConfigUtil.getCollectionValueFields(x).isEmpty()),
            ConfigReloadCommand::new,
            ConfigReloadCommand::new),
    List("list",
            BaseConfig::isEnableList,
            x -> Stream.concat(ConfigUtil.getSingleValues(x).stream(), ConfigUtil.getCollectionValues(x).stream()).anyMatch(Value::listable),
            ConfigListCommand::new,
            ConfigListCommand::new),
    Modify("modify",
            BaseConfig::isEnableModify,
            x -> ConfigUtil.getCollectionValues(x).stream()
                    .anyMatch(v -> v.addableByCommand() || v.removableByCommand() || v.clearableByCommand()) || ConfigUtil.getSingleValues(x).stream().anyMatch(SingleValue::writableByCommand),
            ConfigModifyCommand::new,
            ConfigModifyCommand::new),
    Get("get",
            BaseConfig::isEnableGet,
            x -> Stream.concat(ConfigUtil.getSingleValues(x).stream(), ConfigUtil.getCollectionValues(x).stream()).anyMatch(Value::listable),
            ConfigGetCommand::new,
            ConfigGetCommand::new);

    public final String name;
    private final Predicate<BaseConfig> isEnableFor;
    private final Predicate<BaseConfig> hasEntryFor;
    private final Function<BaseConfig, Command> instantiator;
    private final Function<Set<BaseConfig>, Command> instantiator2;

    SubCommandType(String name, Predicate<BaseConfig> isEnableFor, Predicate<BaseConfig> hasEntryFor, Function<BaseConfig, Command> instantiator, Function<Set<BaseConfig>, Command> instantiator2) {
        this.name = name;
        this.isEnableFor = isEnableFor;
        this.hasEntryFor = hasEntryFor;
        this.instantiator = instantiator;
        this.instantiator2 = instantiator2;
    }

    public boolean hasEntryFor(BaseConfig config) {
        return hasEntryFor.test(config);
    }

    public boolean isEnableFor(BaseConfig config) {
        return isEnableFor.test(config);
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
