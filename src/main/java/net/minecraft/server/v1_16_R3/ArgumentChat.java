package net.minecraft.server.v1_16_R3;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class ArgumentChat implements ArgumentType<ArgumentChat.a> {
    public static ArgumentChat a() {
        return new ArgumentChat();
    }

    public static IChatBaseComponent a(CommandContext<CommandListenerWrapper> ctx, String s) throws CommandSyntaxException {
        return null;
    }

    @Override
    public ArgumentChat.a parse(StringReader reader) throws CommandSyntaxException {
        return null;
    }

    public static class a {

    }
}
