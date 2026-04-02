package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.argument.BooleanArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.util.ListUtil;
import net.minecraft.scoreboard.ScorePlayerTeam;

import java.util.List;

public class Team2BooleanMapValue extends Team2ObjectMapValue<Boolean, Team2BooleanMapValue> {
    @Override
    protected List<PutArgumentDefinition<ScorePlayerTeam, Boolean>> argumentDefinitionsForPut() {
        return ListUtil.of(new PutArgumentDefinition<>(keyArgumentDefinitionForPut(),
                                                       new ArgumentDefinition<>(new BooleanArgument("bool"),
                                                                                (b, ctx) -> b)));
    }

    @Override
    protected String valueToString(Boolean bool) {
        return bool.toString();
    }
}
