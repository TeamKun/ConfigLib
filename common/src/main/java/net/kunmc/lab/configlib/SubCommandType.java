package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.configlib.util.ConfigUtil;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

enum SubCommandType {
    Reload("reload",
           CommonBaseConfig::isReloadEnabled,
           x -> !(ConfigUtil.getSingleValueFields(x)
                            .isEmpty() && ConfigUtil.getCollectionValueFields(x)
                                                    .isEmpty() && ConfigUtil.getMapValueFields(x)
                                                                            .isEmpty()),
           ConfigReloadCommand::new,
           ConfigReloadCommand::new),
    List("list",
         CommonBaseConfig::isListEnabled,
         x -> Stream.of(ConfigUtil.getSingleValues(x)
                                  .stream(),
                        ConfigUtil.getCollectionValues(x)
                                  .stream(),
                        ConfigUtil.getMapValues(x)
                                  .stream())
                    .reduce(Stream::concat)
                    .orElseGet(Stream::empty)
                    .anyMatch(Value::listable),
         ConfigListCommand::new,
         ConfigListCommand::new),
    Modify("modify",
           CommonBaseConfig::isModifyEnabled,
           x -> ConfigUtil.getSingleValues(x)
                          .stream()
                          .anyMatch(SingleValue::writableByCommand) || ConfigUtil.getCollectionValues(x)
                                                                                 .stream()
                                                                                 .anyMatch(v -> v.addableByCommand() || v.removableByCommand() || v.clearableByCommand()) || ConfigUtil.getMapValues(
                                                                                                                                                                                               x)
                                                                                                                                                                                       .stream()
                                                                                                                                                                                       .anyMatch(
                                                                                                                                                                                               v -> v.puttableByCommand() || v.removableByCommand() || v.clearableByCommand()),
           ConfigModifyCommand::new,
           ConfigModifyCommand::new),
    Get("get",
        CommonBaseConfig::isGetEnabled,
        x -> Stream.of(ConfigUtil.getSingleValues(x)
                                 .stream(),
                       ConfigUtil.getCollectionValues(x)
                                 .stream(),
                       ConfigUtil.getMapValues(x)
                                 .stream())
                   .reduce(Stream::concat)
                   .orElseGet(Stream::empty)
                   .anyMatch(Value::listable),
        ConfigGetCommand::new,
        ConfigGetCommand::new);

    public final String name;
    private final Predicate<CommonBaseConfig> isEnabledFor;
    private final Predicate<CommonBaseConfig> hasEntryFor;
    private final Function<CommonBaseConfig, Command> instantiator;
    private final Function<Set<CommonBaseConfig>, Command> instantiator2;

    SubCommandType(String name,
                   Predicate<CommonBaseConfig> isEnabledFor,
                   Predicate<CommonBaseConfig> hasEntryFor,
                   Function<CommonBaseConfig, Command> instantiator,
                   Function<Set<CommonBaseConfig>, Command> instantiator2) {
        this.name = name;
        this.isEnabledFor = isEnabledFor;
        this.hasEntryFor = hasEntryFor;
        this.instantiator = instantiator;
        this.instantiator2 = instantiator2;
    }

    public boolean hasEntryFor(CommonBaseConfig config) {
        return hasEntryFor.test(config);
    }

    public boolean isEnabledFor(CommonBaseConfig config) {
        return isEnabledFor.test(config);
    }

    public Map<CommonBaseConfig, Boolean> hasEntryFor(Set<CommonBaseConfig> configSet) {
        return configSet.stream()
                        .collect(Collectors.toMap(baseConfig -> baseConfig, this::hasEntryFor));
    }

    public Command of(CommonBaseConfig config) {
        return instantiator.apply(config);
    }

    public Command of(Set<CommonBaseConfig> config) {
        return instantiator2.apply(config);
    }
}
