package net.kunmc.lab.sampleplugin;

import dev.kotx.flylib.command.Command;
import org.jetbrains.annotations.NotNull;

public class MainCommand extends Command {
    public MainCommand(@NotNull String name, Command configCommand) {
        super(name);
        children(configCommand);
    }
}
