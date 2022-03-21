package net.kunmc.lab.configlib.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.kotx.flylib.command.Argument;
import dev.kotx.flylib.command.ContextAction;
import dev.kotx.flylib.command.SuggestionAction;
import net.minecraft.server.v1_16_R3.ArgumentTile;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_16_R3.block.data.CraftBlockData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TileArgument implements Argument<BlockData> {
    private final String name;
    private final SuggestionAction suggestionAction;
    private final ContextAction contextAction;

    public TileArgument(String name) {
        this(name, null);
    }

    public TileArgument(String name, SuggestionAction suggestionAction) {
        this(name, suggestionAction, null);
    }

    public TileArgument(String name, SuggestionAction suggestionAction, ContextAction contextAction) {
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
        return ArgumentTile.a();
    }

    @Override
    public BlockData parse(@NotNull CommandContext<CommandListenerWrapper> commandContext, @NotNull String s) {
        return CraftBlockData.createData(ArgumentTile.a(commandContext, s).a());
    }
}
