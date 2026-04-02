package net.kunmc.lab.configlib.util.function;

import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.exception.ArgumentValidationException;

@FunctionalInterface
public interface TriArgumentMapper<A, B, R> {
    R apply(A a, B b, CommandContext ctx) throws ArgumentValidationException;
}
