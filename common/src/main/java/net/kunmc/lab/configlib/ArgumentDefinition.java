package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.CommonArgument;
import net.kunmc.lab.commandlib.exception.ArgumentValidationException;
import net.kunmc.lab.configlib.util.function.*;

/**
 * Defines how arguments are applied to a builder and mapped to a value.
 * The mapper may throw {@link net.kunmc.lab.commandlib.exception.ArgumentValidationException}
 * to send an error message to the command executor.
 */
public class ArgumentDefinition<T> implements ArgumentApplier, ArgumentMapper<T> {
    private final ArgumentApplier argumentApplier;
    private final ArgumentMapper<T> mapper;

    public ArgumentDefinition(ArgumentApplier argumentApplier, ArgumentMapper<T> mapper) {
        this.argumentApplier = argumentApplier;
        this.mapper = mapper;
    }

    public <A1> ArgumentDefinition(CommonArgument<A1, CommandContext> argument1, BiArgumentMapper<A1, T> mapper) {
        this(b -> {
            b.customArgument(argument1);
        }, (ctx) -> {
            return mapper.apply(argument1.cast(ctx.getParsedArg(argument1.name())), ctx);
        });
    }

    public <A1, A2> ArgumentDefinition(CommonArgument<A1, CommandContext> argument1,
                                       CommonArgument<A2, CommandContext> argument2,
                                       TriArgumentMapper<A1, A2, T> mapper) {
        this(b -> {
            b.customArgument(argument1);
            b.customArgument(argument2);
        }, (ctx) -> {
            return mapper.apply(argument1.cast(ctx.getParsedArg(argument1.name())),
                                argument2.cast(ctx.getParsedArg(argument2.name())),
                                ctx);
        });
    }

    public <A1, A2, A3> ArgumentDefinition(CommonArgument<A1, CommandContext> argument1,
                                           CommonArgument<A2, CommandContext> argument2,
                                           CommonArgument<A3, CommandContext> argument3,
                                           QuadArgumentMapper<A1, A2, A3, T> mapper) {
        this(b -> {
            b.customArgument(argument1);
            b.customArgument(argument2);
            b.customArgument(argument3);
        }, (ctx) -> {
            return mapper.apply(argument1.cast(ctx.getParsedArg(argument1.name())),
                                argument2.cast(ctx.getParsedArg(argument2.name())),
                                argument3.cast(ctx.getParsedArg(argument3.name())),
                                ctx);
        });
    }

    public <A1, A2, A3, A4> ArgumentDefinition(CommonArgument<A1, CommandContext> argument1,
                                               CommonArgument<A2, CommandContext> argument2,
                                               CommonArgument<A3, CommandContext> argument3,
                                               CommonArgument<A4, CommandContext> argument4,
                                               QuintArgumentMapper<A1, A2, A3, A4, T> mapper) {
        this(b -> {
            b.customArgument(argument1);
            b.customArgument(argument2);
            b.customArgument(argument3);
            b.customArgument(argument4);
        }, (ctx) -> {
            return mapper.apply(argument1.cast(ctx.getParsedArg(argument1.name())),
                                argument2.cast(ctx.getParsedArg(argument2.name())),
                                argument3.cast(ctx.getParsedArg(argument3.name())),
                                argument4.cast(ctx.getParsedArg(argument4.name())),
                                ctx);
        });
    }

    public <A1, A2, A3, A4, A5> ArgumentDefinition(CommonArgument<A1, CommandContext> argument1,
                                                   CommonArgument<A2, CommandContext> argument2,
                                                   CommonArgument<A3, CommandContext> argument3,
                                                   CommonArgument<A4, CommandContext> argument4,
                                                   CommonArgument<A5, CommandContext> argument5,
                                                   SextArgumentMapper<A1, A2, A3, A4, A5, T> mapper) {
        this(b -> {
            b.customArgument(argument1);
            b.customArgument(argument2);
            b.customArgument(argument3);
            b.customArgument(argument4);
            b.customArgument(argument5);
        }, (ctx) -> {
            return mapper.apply(argument1.cast(ctx.getParsedArg(argument1.name())),
                                argument2.cast(ctx.getParsedArg(argument2.name())),
                                argument3.cast(ctx.getParsedArg(argument3.name())),
                                argument4.cast(ctx.getParsedArg(argument4.name())),
                                argument5.cast(ctx.getParsedArg(argument5.name())),
                                ctx);
        });
    }

    public <A1, A2, A3, A4, A5, A6> ArgumentDefinition(CommonArgument<A1, CommandContext> argument1,
                                                       CommonArgument<A2, CommandContext> argument2,
                                                       CommonArgument<A3, CommandContext> argument3,
                                                       CommonArgument<A4, CommandContext> argument4,
                                                       CommonArgument<A5, CommandContext> argument5,
                                                       CommonArgument<A6, CommandContext> argument6,
                                                       SeptArgumentMapper<A1, A2, A3, A4, A5, A6, T> mapper) {
        this(b -> {
            b.customArgument(argument1);
            b.customArgument(argument2);
            b.customArgument(argument3);
            b.customArgument(argument4);
            b.customArgument(argument5);
            b.customArgument(argument6);
        }, (ctx) -> {
            return mapper.apply(argument1.cast(ctx.getParsedArg(argument1.name())),
                                argument2.cast(ctx.getParsedArg(argument2.name())),
                                argument3.cast(ctx.getParsedArg(argument3.name())),
                                argument4.cast(ctx.getParsedArg(argument4.name())),
                                argument5.cast(ctx.getParsedArg(argument5.name())),
                                argument6.cast(ctx.getParsedArg(argument6.name())),
                                ctx);
        });
    }

    public void applyArgument(ArgumentBuilder builder) {
        argumentApplier.applyArgument(builder);
    }

    public T mapArgument(CommandContext ctx) throws ArgumentValidationException {
        return mapper.mapArgument(ctx);
    }
}
