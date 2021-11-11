package net.kunmc.lab.command;

import dev.kotx.flylib.command.Command;
import dev.kotx.flylib.command.CommandContext;
import net.kunmc.lab.config.BaseConfig;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

class ConfigReloadCommand extends Command {
    private final List<BaseConfig> configList = new ArrayList<>();

    public ConfigReloadCommand(@NotNull BaseConfig config) {
        super(SubCommand.Reload.name);
        configList.add(config);
    }

    public ConfigReloadCommand(@NotNull List<BaseConfig> configList) {
        super(SubCommand.Reload.name);
       
        if (configList.isEmpty()) {
            throw new IllegalArgumentException("configList is empty");
        }
        this.configList.addAll(configList);

        for (BaseConfig config : configList) {
            children(new Command(config.entryName()) {
                @Override
                public void execute(@NotNull CommandContext ctx) {
                    exec(ctx, config);
                }
            });
        }
    }

    @Override
    public void execute(CommandContext ctx) {
        configList.forEach(x -> exec(ctx, x));
    }

    private void exec(CommandContext ctx, BaseConfig config) {
        String fileName = config.configJSON.getName();
        if (config.loadConfig()) {
            ctx.success(fileName + "を再読み込みしました.");
        } else {
            ctx.fail(fileName + "の読み込みに失敗しました.ファイルが存在しません.");
        }
    }
}
