package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.configlib.MapValue;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Team2ObjectMapValue<V, T extends Team2ObjectMapValue<V, T>> extends MapValue<Team, V, T> {
    private final transient Scoreboard scoreboard;

    public Team2ObjectMapValue() {
        this(new HashMap<>());
    }

    public Team2ObjectMapValue(Map<Team, V> map) {
        super(map);

        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    }

    @Override
    protected void appendKeyArgumentForPut(ArgumentBuilder builder) {
        builder.teamArgument("team");
    }

    @Override
    protected boolean isCorrectKeyArgumentForPut(String entryName, List<Object> argument, CommandSender sender) {
        return argument.get(0) != null;
    }

    @Override
    protected String incorrectKeyArgumentMessageForPut(String entryName, List<Object> argument, CommandSender sender) {
        return "指定されたチームは存在しません.";
    }

    @Override
    protected Team argumentToKeyForPut(List<Object> argument, CommandSender sender) {
        return ((Team) argument.get(0));
    }

    @Override
    protected boolean validateKeyForPut(String entryName, Team team, CommandSender sender) {
        return true;
    }

    @Override
    protected String invalidKeyMessageForPut(String entryName, Team team, CommandSender sender) {
        return "This message can't be shown.";
    }

    @Override
    protected void appendKeyArgumentForRemove(ArgumentBuilder builder) {
        builder.teamArgument("team");
    }

    @Override
    protected boolean isCorrectKeyArgumentForRemove(String entryName, List<Object> argument, CommandSender sender) {
        return argument.get(0) != null;
    }

    @Override
    protected String incorrectKeyArgumentMessageForRemove(String entryName, List<Object> argument, CommandSender sender) {
        return "指定されたチームは存在しません.";
    }

    @Override
    protected Team argumentToKeyForRemove(List<Object> argument, CommandSender sender) {
        return ((Team) argument.get(0));
    }

    @Override
    protected String keyToString(Team team) {
        return team.getName();
    }
}
