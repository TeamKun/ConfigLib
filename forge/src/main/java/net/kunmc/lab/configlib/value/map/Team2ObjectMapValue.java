package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.configlib.MapValue;
import net.minecraft.command.CommandSource;
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

    @Override
    protected void appendKeyArgumentForPut(ArgumentBuilder builder) {
        builder.teamArgument("team");
    }

    @Override
    protected boolean isCorrectKeyArgumentForPut(List<Object> argument, CommandSource sender) {
        return argument.get(0) != null;
    }

    @Override
    protected String incorrectKeyArgumentMessageForPut(List<Object> argument) {
        return "指定されたチームは存在しません.";
    }

    @Override
    protected ScorePlayerTeam argumentToKeyForPut(List<Object> argument, CommandSource sender) {
        return ((ScorePlayerTeam) argument.get(0));
    }

    @Override
    protected boolean validateKeyForPut(ScorePlayerTeam team) {
        return true;
    }

    @Override
    protected String invalidKeyMessageForPut(String entryName, ScorePlayerTeam team) {
        return "This message can't be shown.";
    }

    @Override
    protected void appendKeyArgumentForRemove(ArgumentBuilder builder) {
        builder.teamArgument("team");
    }

    @Override
    protected boolean isCorrectKeyArgumentForRemove(List<Object> argument, CommandSource sender) {
        return argument.get(0) != null;
    }

    @Override
    protected String incorrectKeyArgumentMessageForRemove(List<Object> argument) {
        return "指定されたチームは存在しません.";
    }

    @Override
    protected ScorePlayerTeam argumentToKeyForRemove(List<Object> argument, CommandSource sender) {
        return ((ScorePlayerTeam) argument.get(0));
    }

    @Override
    protected String keyToString(ScorePlayerTeam team) {
        if (team == null) {
            return "null";
        }
       
        return team.getName();
    }
}