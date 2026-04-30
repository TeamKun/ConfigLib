package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

enum SubCommandType {
    Reload("reload",
           CommonBaseConfig::isReloadEnabled,
           x -> !x.schema()
                  .entries()
                  .isEmpty(),
           ConfigReloadCommand::new),
    Reset("reset",
          CommonBaseConfig::isResetEnabled,
          x -> !x.schema()
                 .entries()
                 .isEmpty(),
          ConfigResetCommand::new),
    History("history", CommonBaseConfig::isHistoryEnabled, x -> true, ConfigHistoryCommand::new),
    Audit("audit", CommonBaseConfig::isHistoryEnabled, x -> true, ConfigAuditCommand::new),
    Undo("undo", CommonBaseConfig::isHistoryEnabled, x -> true, ConfigUndoCommand::new),
    Diff("diff", CommonBaseConfig::isHistoryEnabled, x -> true, ConfigDiffCommand::new),
    List("list",
         CommonBaseConfig::isListEnabled,
         x -> !x.schema()
                .entries()
                .isEmpty(),
         ConfigListCommand::new);

    public final String name;
    private final Predicate<CommonBaseConfig> isEnabledFor;
    private final Predicate<CommonBaseConfig> hasEntryFor;
    private final BiFunction<Set<CommonBaseConfig>, ConfigCommandDescriptions.Provider, Command> instantiator;

    SubCommandType(String name,
                   Predicate<CommonBaseConfig> isEnabledFor,
                   Predicate<CommonBaseConfig> hasEntryFor,
                   BiFunction<Set<CommonBaseConfig>, ConfigCommandDescriptions.Provider, Command> instantiator) {
        this.name = name;
        this.isEnabledFor = isEnabledFor;
        this.hasEntryFor = hasEntryFor;
        this.instantiator = instantiator;
    }

    public boolean hasEntryFor(CommonBaseConfig config) {
        return hasEntryFor.test(config);
    }

    public boolean isEnabledFor(CommonBaseConfig config) {
        return isEnabledFor.test(config);
    }

    public Map<CommonBaseConfig, Boolean> hasEntryFor(List<CommonBaseConfig> configs) {
        return configs.stream()
                      .collect(Collectors.toMap(baseConfig -> baseConfig, this::hasEntryFor, (a, b) -> {
                          throw new IllegalStateException();
                      }, LinkedHashMap::new));
    }

    public Command of(Set<CommonBaseConfig> configs, ConfigCommandDescriptions.Provider descriptions) {
        return instantiator.apply(configs, descriptions);
    }
}
