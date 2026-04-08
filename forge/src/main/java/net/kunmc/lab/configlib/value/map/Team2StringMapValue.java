package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.argument.StringArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.minecraft.scoreboard.ScorePlayerTeam;

import java.util.List;

public class Team2StringMapValue extends Team2ObjectMapValue<String, Team2StringMapValue> {
    @Override
    protected List<PutArgumentDefinition<ScorePlayerTeam, String>> argumentDefinitionsForPut() {
        return List.of(new PutArgumentDefinition<>(keyArgumentDefinitionForPut(),
                                                   new ArgumentDefinition<>(new StringArgument("string",
                                                                                               StringArgument.Type.PHRASE),
                                                                            (s, ctx) -> s)));
    }

    @Override
    protected String valueToString(String s) {
        return s;
    }
}
