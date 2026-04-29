package net.kunmc.lab.configlib.value.collection;

import net.kunmc.lab.commandlib.argument.TeamArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * If you need completions on "add" with NewScoreboard, you should register it with scoreboard(Scoreboard) method.
 * When you use NewScoreboard, this class can't be deserialized. Therefore, a field must be qualified as transient at that time.
 */
public class TeamSetValue extends SetValue<ScorePlayerTeam, TeamSetValue> {
    public TeamSetValue(ScorePlayerTeam... teams) {
        this(new HashSet<>(List.of(teams)));
    }

    public TeamSetValue(@NotNull Set<ScorePlayerTeam> value) {
        super(value);
    }

    @Override
    protected List<ArgumentDefinition<Set<ScorePlayerTeam>>> argumentDefinitionsForAdd() {
        return List.of(new ArgumentDefinition<>(new TeamArgument("team").suggestionAction(sb -> {
            ServerLifecycleHooks.getCurrentServer()
                                .getScoreboard()
                                .getTeams()
                                .stream()
                                .map(Team::getName)
                                .filter(name -> value.stream()
                                                     .map(Team::getName)
                                                     .noneMatch(s -> s.equals(name)))
                                .forEach(sb::suggest);
        }), (team, ctx) -> Set.of(team)));
    }

    @Override
    protected List<ArgumentDefinition<Set<ScorePlayerTeam>>> argumentDefinitionsForRemove() {
        return List.of(new ArgumentDefinition<>(new TeamArgument("team").suggestionAction(sb -> {
            value.stream()
                 .map(Team::getName)
                 .forEach(sb::suggest);
        }), (team, ctx) -> Set.of(team)));
    }

    @Override
    protected String elementToString(ScorePlayerTeam team) {
        if (team == null) {
            return "null";
        }

        return team.getName();
    }
}
