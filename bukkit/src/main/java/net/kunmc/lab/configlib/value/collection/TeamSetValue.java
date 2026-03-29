package net.kunmc.lab.configlib.value.collection;

import net.kunmc.lab.commandlib.argument.TeamArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.util.ListUtil;
import net.kunmc.lab.configlib.util.SetUtil;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TeamSetValue extends SetValue<Team, TeamSetValue> {
    private final transient Scoreboard scoreboard;

    public TeamSetValue(Team... teams) {
        this(new HashSet<>(Arrays.asList(teams)));
    }

    public TeamSetValue(@NotNull Set<Team> value) {
        super(value);
        scoreboard = Bukkit.getScoreboardManager()
                           .getMainScoreboard();
    }

    public @NotNull Scoreboard scoreboard() {
        return scoreboard;
    }

    @Override
    protected List<ArgumentDefinition<Set<Team>>> argumentDefinitionsForAdd() {
        return ListUtil.of(new ArgumentDefinition<>(new TeamArgument("team", opt -> {
            opt.suggestionAction(sb -> {
                scoreboard.getTeams()
                          .stream()
                          .map(Team::getName)
                          .filter(name -> value.stream()
                                               .map(Team::getName)
                                               .noneMatch(s -> s.equals(name)))
                          .forEach(sb::suggest);
            });
        }), (team, ctx) -> SetUtil.newHashSet(team)));
    }

    @Override
    protected List<ArgumentDefinition<Set<Team>>> argumentDefinitionsForRemove() {
        return ListUtil.of(new ArgumentDefinition<>(new TeamArgument("team", opt -> {
            opt.suggestionAction(sb -> {
                value.stream()
                     .map(Team::getName)
                     .forEach(sb::suggest);
            });
        }), (team, ctx) -> SetUtil.newHashSet(team)));
    }

    @Override
    protected String elementToString(Team team) {
        if (team == null) {
            return "null";
        }

        return team.getName();
    }
}
