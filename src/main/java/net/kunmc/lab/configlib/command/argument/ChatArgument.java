package net.kunmc.lab.configlib.command.argument;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.kotx.flylib.command.Argument;
import dev.kotx.flylib.command.SuggestionAction;
import net.minecraft.server.v1_16_R3.ArgumentChat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChatArgument implements Argument<String> {
    private final String name;
    private final SuggestionAction suggestionAction;

    public ChatArgument(String name, @Nullable SuggestionAction suggestionAction) {
        this.name = name;
        this.suggestionAction = suggestionAction;
    }

    @Nullable
    @Override
    public com.mojang.brigadier.arguments.ArgumentType<?> getType() {
        return ArgumentChat.a();
    }

    @Override
    public String parse(@NotNull com.mojang.brigadier.context.CommandContext<net.minecraft.server.v1_16_R3.CommandListenerWrapper> commandContext, @NotNull String s) {
        try {
            return ArgumentChat.a(commandContext, s).getString();
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
            return null;
        }
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
}
