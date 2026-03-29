package net.kunmc.lab.configlib.util.function;

import net.kunmc.lab.commandlib.CommandContext;

@FunctionalInterface
public interface ArgumentMapper<T> {
    /**
     * Defines how arguments are mapped to a value.
     * This may throw {@link net.kunmc.lab.commandlib.exception.InvalidArgumentException}
     * to send an error message to the command executor.
     */
    T mapArgument(CommandContext ctx);
}
