package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.SingleValue;
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
    protected ScorePlayerTeam argumentToValue(List<Object> argument, CommandContext ctx) {
        return ((ScorePlayerTeam) argument.get(0));
    }

    @Override
    protected String valueToString(ScorePlayerTeam team) {
        return team.getName();
    }
}
