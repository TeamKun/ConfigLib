package net.kunmc.lab.command;

import dev.kotx.flylib.command.Command;
import dev.kotx.flylib.command.CommandContext;
import net.kunmc.lab.config.BaseConfig;

import java.io.FileNotFoundException;

class ConfigReloadCommand extends Command {
    private final BaseConfig config;

    public ConfigReloadCommand(BaseConfig config) {
        super("reload");
        this.config = config;
    }

    @Override
    public void execute(CommandContext ctx) {
        String fileName = config.configJSON.getName();
        try {
            config.loadConfig();
            ctx.success(fileName + "を再読み込みしました.");
        } catch (FileNotFoundException e) {
            ctx.fail(fileName + "の読み込みに失敗しました.ファイルが存在しません.");
        }
    }
}
