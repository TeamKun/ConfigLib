package net.kunmc.lab.configlib.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.kotx.flylib.command.Argument;
import dev.kotx.flylib.command.SuggestionAction;
import net.minecraft.server.v1_16_R3.ArgumentChat;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UnparsedArgument implements Argument<String> {
    private final String name;
    private final SuggestionAction suggestionAction;

    public UnparsedArgument(String name, SuggestionAction suggestionAction) {
        this.name = name;
        this.suggestionAction = suggestionAction;
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
}
