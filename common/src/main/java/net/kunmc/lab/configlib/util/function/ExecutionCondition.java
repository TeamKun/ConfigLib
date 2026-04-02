package net.kunmc.lab.configlib.util.function;

import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.exception.CommandPrerequisiteException;

@FunctionalInterface
public interface ExecutionCondition {
    void check(CommandContext ctx) throws CommandPrerequisiteException;
}
