package net.kunmc.lab.configlib.value.collection;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.util.SetUtil;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * If you need completions on "add" with NewScoreboard, you should register it with scoreboard(Scoreboard) method.
 * When you use NewScoreboard, this class can't be deserialized. Therefore, a field must be qualified as transient at that time.
 */
public class TeamSetValue extends SetValue<ScorePlayerTeam, TeamSetValue> {
    public TeamSetValue(ScorePlayerTeam... teams) {
        this(new HashSet<>(Arrays.asList(teams)));
    }

    public TeamSetValue(@NotNull Set<ScorePlayerTeam> value) {
        super(value);
    }

    @Override
    protected void appendArgumentForAdd(ArgumentBuilder builder) {
        builder.teamArgument("name", sb -> {
            ServerLifecycleHooks.getCurrentServer()
                                .getScoreboard()
                                .getTeams()
                                .stream()
                                .map(Team::getName)
                                .filter(name -> value.stream()
                                                     .map(Team::getName)
                                                     .noneMatch(s -> s.equals(name)))
                                .forEach(sb::suggest);
        });
    }

    @Override
    protected Set<ScorePlayerTeam> argumentToValueForAdd(String entryName, List<Object> argument, CommandContext ctx) {
        return SetUtil.newHashSet(((ScorePlayerTeam) argument.get(0)));
    }

    @Override
    protected void appendArgumentForRemove(ArgumentBuilder builder) {
        builder.teamArgument("team", suggestionBuilder -> {
            value.stream()
                 .map(Team::getName)
                 .forEach(suggestionBuilder::suggest);
        });
    }

    @Override
    protected Set<ScorePlayerTeam> argumentToValueForRemove(String entryName,
                                                            List<Object> argument,
                                                            CommandContext ctx) {
        return SetUtil.newHashSet(((ScorePlayerTeam) argument.get(0)));
    }

    @Override
    protected String elementToString(ScorePlayerTeam team) {
        if (team == null) {
            return "null";
        }

        return team.getName();
    }
}
