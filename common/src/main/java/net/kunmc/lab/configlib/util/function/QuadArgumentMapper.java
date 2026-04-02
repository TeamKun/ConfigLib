package net.kunmc.lab.configlib.util.function;

import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.exception.ArgumentValidationException;

@FunctionalInterface
public interface QuadArgumentMapper<A, B, C, R> {
    R apply(A a, B b, C c, CommandContext ctx) throws ArgumentValidationException;
}
