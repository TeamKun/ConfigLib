package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class ConfigReloadCommand extends Command {
    private final List<BaseConfig> configs = new ArrayList<>();

    public ConfigReloadCommand(@NotNull BaseConfig config) {
        super(SubCommandType.Reload.name);
        configs.add(config);
    }

    public ConfigReloadCommand(@NotNull Set<BaseConfig> configSet) {
        super(SubCommandType.Reload.name);

        if (configSet.isEmpty()) {
            throw new IllegalArgumentException("configSet is empty");
        }
        this.configs.addAll(configSet);

        for (BaseConfig config : configSet) {
            addChildren(new Command(config.entryName()) {
                @Override
                public void execute(@NotNull CommandContext ctx) {
                    exec(ctx, config);
                }
            });
        }
    }

    @Override
    public void execute(CommandContext ctx) {
        configs.forEach(x -> exec(ctx, x));
    }

    private void exec(CommandContext ctx, BaseConfig config) {
        String fileName = config.getConfigFile().getName();
        if (config.loadConfig()) {
            ctx.sendSuccess(fileName + "を再読み込みしました.");
        } else {
            ctx.sendFailure(fileName + "の読み込みに失敗しました.ファイルが存在しません.");
        }
    }
}
