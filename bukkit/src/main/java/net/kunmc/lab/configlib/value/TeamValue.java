package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.SingleValue;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TeamValue extends SingleValue<Team, TeamValue> {
    private final transient Scoreboard scoreboard;

    public TeamValue() {
        this(null);
    }

    public TeamValue(Team value) {
        super(value);
        scoreboard = Bukkit.getScoreboardManager()
                           .getMainScoreboard();
    }

    public @NotNull Scoreboard scoreboard() {
        return scoreboard;
    }

    @Override
    protected void appendArgument(ArgumentBuilder builder) {
        builder.teamArgument("team");
    }

    @Override
    protected Team argumentToValue(List<Object> argument, CommandContext ctx) {
        return ((Team) argument.get(0));
    }

    @Override
    protected String valueToString(Team team) {
        return team.getName();
    }
}
