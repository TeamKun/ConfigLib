package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.MapValue;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Team2ObjectMapValue<V, T extends Team2ObjectMapValue<V, T>> extends MapValue<Team, V, T> {
    public Team2ObjectMapValue() {
        this(new HashMap<>());
    }

    public Team2ObjectMapValue(Map<Team, V> map) {
        super(map);
    }

    @Override
    protected void appendKeyArgumentForPut(ArgumentBuilder builder) {
        builder.teamArgument("team");
    }

    @Override
    protected Team argumentToKeyForPut(List<Object> argument, CommandContext ctx) {
        return ((Team) argument.get(0));
    }

    @Override
    protected void appendKeyArgumentForRemove(ArgumentBuilder builder) {
        builder.teamArgument("team");
    }

    @Override
    protected Team argumentToKeyForRemove(List<Object> argument, CommandContext ctx) {
        return ((Team) argument.get(0));
    }

    @Override
    protected String keyToString(Team team) {
        return team.getName();
    }
}
