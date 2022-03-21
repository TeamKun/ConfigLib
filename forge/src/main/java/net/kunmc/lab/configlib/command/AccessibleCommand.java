package net.kunmc.lab.configlib.command;

import dev.kotx.flylib.command.Command;
import dev.kotx.flylib.command.Permission;
import dev.kotx.flylib.command.UsageAction;
import org.jetbrains.annotations.NotNull;

public class AccessibleCommand extends Command {
    public AccessibleCommand(@NotNull String name) {
        super(name);
    }

    public void appendChild(Command command) {
        children(command);
    }

    public void addAlias(String alias) {
        alias(alias);
    }

    public void addExample(String example) {
        example(example);
    }

    public void addUsage(UsageAction action) {
        usage(action);
    }

    public void setDescription(String description) {
        description(description);
    }

    public void setPermission(Permission permission) {
        permission(permission);
    }
}
