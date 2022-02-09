package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;
import dev.kotx.flylib.command.arguments.StringArgument;
import net.kunmc.lab.configlib.MapValue;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Team2ObjectMapValue<V, T extends Team2ObjectMapValue<V, T>> extends MapValue<Team, V, T> {
    private transient Scoreboard scoreboard;

    public Team2ObjectMapValue() {
        this(new HashMap<>());
    }

    public Team2ObjectMapValue(Map<Team, V> map) {
        super(map);

        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    }

    public @NotNull Scoreboard scoreboard() {
        return scoreboard;
    }

    public T scoreboard(@NotNull Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
        return ((T) this);
    }

    @Override
    protected void appendKeyArgumentForPut(UsageBuilder builder) {
        builder.stringArgument("team", StringArgument.Type.PHRASE_QUOTED, suggestionBuilder -> {
            scoreboard.getTeams().stream()
                    .map(Team::getName)
                    .forEach(suggestionBuilder::suggest);
        });
    }

    @Override
    protected boolean isCorrectKeyArgumentForPut(List<Object> argument, CommandSender sender) {
        return scoreboard.getTeams().stream()
                .map(Team::getName)
                .anyMatch(s -> s.equals(argument.get(0)));
    }

    @Override
    protected String incorrectKeyArgumentMessageForPut(List<Object> argument) {
        return argument.get(0) + "は存在しないチームです.";
    }

    @Override
    protected Team argumentToKeyForPut(List<Object> argument, CommandSender sender) {
        return scoreboard.getTeam(argument.get(0).toString());
    }

    @Override
    protected boolean validateKeyForPut(Team team) {
        return true;
    }

    @Override
    protected String invalidKeyMessageForPut(String entryName, Team team) {
        return "This message can't be shown.";
    }

    @Override
    protected void appendKeyArgumentForRemove(UsageBuilder builder) {
        builder.stringArgument("team", StringArgument.Type.PHRASE_QUOTED, sb -> {
            value.keySet().stream()
                    .map(Team::getName)
                    .forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectKeyArgumentForRemove(List<Object> argument, CommandSender sender) {
        return scoreboard.getTeams().stream()
                .map(Team::getName)
                .anyMatch(s -> s.equals(argument.get(0)));
    }

    @Override
    protected String incorrectKeyArgumentMessageForRemove(List<Object> argument) {
        return argument.get(0) + "は存在しないチームです.";
    }

    @Override
    protected Team argumentToKeyForRemove(List<Object> argument, CommandSender sender) {
        return scoreboard.getTeam(argument.get(0).toString());
    }

    @Override
    protected String keyToString(Team team) {
        return team.getName();
    }
}
