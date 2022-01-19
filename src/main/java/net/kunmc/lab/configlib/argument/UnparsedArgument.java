package net.kunmc.lab.configlib.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.kotx.flylib.command.Argument;
import dev.kotx.flylib.command.ContextAction;
import dev.kotx.flylib.command.SuggestionAction;
import net.minecraft.server.v1_16_R3.ArgumentChat;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
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
        return ArgumentChat.a();
    }

    @Override
    public String parse(@NotNull CommandContext<CommandListenerWrapper> commandContext, @NotNull String s) {
        String[] splitted = commandContext.getInput().split(" ");
        return splitted[splitted.length - 1];
    }

    @Nullable
    @Override
    public ContextAction getAction() {
        return null;
    }
}
