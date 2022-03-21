package net.kunmc.lab.configlib.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.kotx.flylib.command.Argument;
import dev.kotx.flylib.command.ContextAction;
import dev.kotx.flylib.command.SuggestionAction;
import net.minecraft.server.v1_16_R3.ArgumentVec3;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;
import net.minecraft.server.v1_16_R3.Vec3D;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LocationArgument implements Argument<Location> {
    private final String name;
    private final SuggestionAction suggestionAction;
    private final ContextAction contextAction;

    public LocationArgument(String name) {
        this(name, null);
    }

    public LocationArgument(String name, SuggestionAction suggestionAction) {
        this(name, suggestionAction, null);
    }

    public LocationArgument(String name, SuggestionAction suggestionAction, ContextAction contextAction) {
        this.name = name;
        this.suggestionAction = suggestionAction;
        this.contextAction = contextAction;
    }

    @Nullable
    @Override
    public ContextAction getAction() {
        return contextAction;
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
        return ArgumentVec3.a();
    }

    @Override
    public Location parse(@NotNull CommandContext<CommandListenerWrapper> commandContext, @NotNull String s) {
        try {
            Vec3D vec = ArgumentVec3.a(commandContext, s);
            return new Location(null, vec.x, vec.y, vec.z);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}
