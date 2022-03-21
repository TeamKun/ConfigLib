package net.kunmc.lab.configlib.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.StringRange;
import dev.kotx.flylib.command.Argument;
import dev.kotx.flylib.command.ContextAction;
import dev.kotx.flylib.command.SuggestionAction;
import net.minecraft.server.v1_16_R3.ArgumentProfile;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class UnparsedArgument implements Argument<String> {
    private final String name;
    private final SuggestionAction suggestionAction;

    public UnparsedArgument(String name, Supplier<List<String>> suggestionSupplier) {
        this.name = name;
        this.suggestionAction = sb -> {
            sb.suggestAll(suggestionSupplier.get().stream()
                    .filter(s -> s.startsWith(sb.getArgs().get(0)))
                    .collect(Collectors.toList()));
        };
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Nullable
    @Override
    public SuggestionAction getSuggestion() {
        return suggestionAction;
    }

    @Nullable
    @Override
    public ArgumentType<?> getType() {
        return ArgumentProfile.a();
    }

    @Override
    public String parse(@NotNull CommandContext<CommandListenerWrapper> commandContext, @NotNull String s) {
        String input = commandContext.getInput();

        Map<String, ParsedArgument> map = null;
        try {
            Field field = CommandContext.class.getDeclaredField("arguments");
            field.setAccessible(true);
            map = ((Map<String, ParsedArgument>) field.get(commandContext));
        } catch (Exception e) {
            e.printStackTrace();
        }

        ParsedArgument argument = map.get(s);
        if (argument == null) {
            return "";
        }

        StringRange range = argument.getRange();
        return range.get(input);
    }

    @Nullable
    @Override
    public ContextAction getAction() {
        return null;
    }
}
