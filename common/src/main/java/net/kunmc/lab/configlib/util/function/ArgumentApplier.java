package net.kunmc.lab.configlib.util.function;

import net.kunmc.lab.commandlib.ArgumentBuilder;

@FunctionalInterface
public interface ArgumentApplier {
    void applyArgument(ArgumentBuilder builder);
}
