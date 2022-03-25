package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.configlib.SingleValue;
import net.minecraft.command.CommandSource;
import net.minecraft.scoreboard.ScorePlayerTeam;

import java.util.List;

public class TeamValue extends SingleValue<ScorePlayerTeam, TeamValue> {
    public TeamValue() {
        this(null);
    }

    public TeamValue(ScorePlayerTeam value) {
        super(value);
    }

    @Override
    protected void appendArgument(ArgumentBuilder builder) {
        builder.teamArgument("team");
    }

    @Override
    protected boolean isCorrectArgument(String entryName, List<Object> argument, CommandSource sender) {
        return argument.get(0) != null;
    }

    @Override
    protected String incorrectArgumentMessage(String entryName, List<Object> argument, CommandSource sender) {
        return "指定されたチームは存在しません.";
    }

    @Override
    protected ScorePlayerTeam argumentToValue(List<Object> argument, CommandSource sender) {
        return ((ScorePlayerTeam) argument.get(0));
    }

    @Override
    protected boolean validateOnSet(String entryName, ScorePlayerTeam newValue, CommandSource sender) {
        if (value == null) {
            return true;
        }

        return !value.getName().equals(newValue.getName());
    }

    @Override
    protected String invalidValueMessage(String entryName, ScorePlayerTeam newValue, CommandSource sender) {
        return newValue.getName() + "はすでに設定されているチームです.";
    }

    @Override
    protected String valueToString(ScorePlayerTeam team) {
        if (team == null) {
            return "null";
        }

        return team.getName();
    }
}
