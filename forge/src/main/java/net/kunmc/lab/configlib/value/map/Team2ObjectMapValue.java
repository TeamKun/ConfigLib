package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.argument.TeamArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.MapValue;
import net.kunmc.lab.configlib.util.ListUtil;
import net.minecraft.scoreboard.ScorePlayerTeam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Team2ObjectMapValue<V, T extends Team2ObjectMapValue<V, T>> extends MapValue<ScorePlayerTeam, V, T> {
    public Team2ObjectMapValue() {
        this(new HashMap<>());
    }

    public Team2ObjectMapValue(Map<ScorePlayerTeam, V> map) {
        super(map);
    }

    protected ArgumentDefinition<ScorePlayerTeam> keyArgumentDefinitionForPut() {
        return new ArgumentDefinition<>(new TeamArgument("team"), (team, ctx) -> team);
    }

    @Override
    protected List<ArgumentDefinition<ScorePlayerTeam>> argumentDefinitionsForRemove() {
        return ListUtil.of(new ArgumentDefinition<>(new TeamArgument("team"), (team, ctx) -> team));
    }

    @Override
    protected String keyToString(ScorePlayerTeam team) {
        if (team == null) {
            return "null";
        }

        return team.getName();
    }
}
