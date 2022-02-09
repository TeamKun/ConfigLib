package net.kunmc.lab.configlib.value;

import com.google.common.collect.Sets;
import dev.kotx.flylib.command.UsageBuilder;
import dev.kotx.flylib.command.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * If you need completions on "add" with NewScoreboard, you should register it with scoreboard(Scoreboard) method.
 * When you use NewScoreboard, this class can't be deserialized. Therefore, a field must be qualified as transient at that time.
 */
public class TeamSetValue extends SetValue<Team, TeamSetValue> {
    private transient Scoreboard scoreboard;

    public TeamSetValue(Team... teams) {
        this(new HashSet<>(Arrays.asList(teams)));
    }

    public TeamSetValue(@NotNull Set<Team> value) {
        super(value);
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    }

    public @NotNull Scoreboard scoreboard() {
        return scoreboard;
    }

    public TeamSetValue scoreboard(@NotNull Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
        return this;
    }

    @Override
    protected void appendArgumentForAdd(UsageBuilder builder) {
        builder.stringArgument("team", StringArgument.Type.WORD, suggestionBuilder -> {
            scoreboard.getTeams().stream()
                    .map(Team::getName)
                    .filter(name -> value.stream()
                            .map(Team::getName)
                            .noneMatch(s -> s.equals(name)))
                    .forEach(suggestionBuilder::suggest);
        });
    }

    @Override
    protected boolean isCorrectArgumentForAdd(List<Object> argument, CommandSender sender) {
        String name = argument.get(0).toString();
        return scoreboard.getTeams().stream()
                .map(Team::getName)
                .anyMatch(s -> s.equals(name));
    }

    @Override
    protected String incorrectArgumentMessageForAdd(List<Object> argument) {
        return argument.get(0) + "は存在しないチームです";
    }

    @Override
    protected Set<Team> argumentToValueForAdd(List<Object> argument, CommandSender sender) {
        Team t = scoreboard.getTeam(argument.get(0).toString());
        return Sets.newHashSet(t);
    }

    @Override
    protected void appendArgumentForRemove(UsageBuilder builder) {
        builder.stringArgument("team", StringArgument.Type.WORD, suggestionBuilder -> {
            value.stream()
                    .map(Team::getName)
                    .forEach(suggestionBuilder::suggest);
        });
    }

    @Override
    protected boolean isCorrectArgumentForRemove(List<Object> argument, CommandSender sender) {
        String name = argument.get(0).toString();
        return scoreboard.getTeams().stream()
                .map(Team::getName)
                .anyMatch(s -> s.equals(name));
    }

    @Override
    protected String incorrectArgumentMessageForRemove(List<Object> argument) {
        return argument.get(0) + "は存在しないチームです.";
    }

    @Override
    protected Set<Team> argumentToValueForRemove(List<Object> argument, CommandSender sender) {
        Team t = scoreboard.getTeam(argument.get(0).toString());
        return Sets.newHashSet(t);
    }

    @Override
    protected String elementToString(Team team) {
        return team.getName();
    }
}
