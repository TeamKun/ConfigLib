package net.kunmc.lab.configlib.util.function;

import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.exception.ArgumentValidationException;

@FunctionalInterface
public interface QuintArgumentMapper<A, B, C, D, R> {
    R apply(A a, B b, C c, D d, CommandContext ctx) throws ArgumentValidationException;
}
