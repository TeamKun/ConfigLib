package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.argument.TeamArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
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
    protected List<ArgumentDefinition<ScorePlayerTeam>> argumentDefinitions() {
        return List.of(new ArgumentDefinition<>(new TeamArgument("team"), (team, ctx) -> team));
    }

    @Override
    protected String valueToString(ScorePlayerTeam team) {
        return team.getName();
    }
}
