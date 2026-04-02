package net.kunmc.lab.configlib.util.function;

import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.exception.ArgumentValidationException;

@FunctionalInterface
public interface SeptArgumentMapper<A, B, C, D, E, F, R> {
    R apply(A a, B b, C c, D d, E e, F f, CommandContext ctx) throws ArgumentValidationException;
}
