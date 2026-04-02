package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.argument.FloatArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.util.ListUtil;
import net.minecraft.scoreboard.ScorePlayerTeam;

import java.util.List;

public class Team2FloatMapValue extends Team2ObjectMapValue<Float, Team2FloatMapValue> {
    @Override
    protected List<PutArgumentDefinition<ScorePlayerTeam, Float>> argumentDefinitionsForPut() {
        return ListUtil.of(new PutArgumentDefinition<>(keyArgumentDefinitionForPut(),
                                                       new ArgumentDefinition<>(new FloatArgument("float",
                                                                                                  -Float.MAX_VALUE,
                                                                                                  Float.MAX_VALUE),
                                                                                (f, ctx) -> f)));
    }

    @Override
    protected String valueToString(Float f) {
        return f.toString();
    }
}
