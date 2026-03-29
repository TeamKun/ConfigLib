package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.argument.DoubleArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.util.ListUtil;
import net.minecraft.scoreboard.ScorePlayerTeam;

import java.util.List;

public class Team2DoubleMapValue extends Team2ObjectMapValue<Double, Team2DoubleMapValue> {
    @Override
    protected List<PutArgumentDefinition<ScorePlayerTeam, Double>> argumentDefinitionsForPut() {
        return ListUtil.of(new PutArgumentDefinition<>(
                keyArgumentDefinitionForPut(),
                new ArgumentDefinition<>(new DoubleArgument("double", -Double.MAX_VALUE, Double.MAX_VALUE),
                                         (d, ctx) -> d)));
    }

    @Override
    protected String valueToString(Double d) {
        return d.toString();
    }
}
