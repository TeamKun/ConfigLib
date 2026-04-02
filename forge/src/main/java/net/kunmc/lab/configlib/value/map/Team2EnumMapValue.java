package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.argument.EnumArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.util.ListUtil;
import net.minecraft.scoreboard.ScorePlayerTeam;

import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class Team2EnumMapValue<T extends Enum<T>> extends Team2ObjectMapValue<T, Team2EnumMapValue<T>> {
    private final transient Class<T> clazz;
    private final transient Predicate<T> filter;

    public Team2EnumMapValue(Class<T> clazz) {
        this(clazz, x -> true);
    }

    public Team2EnumMapValue(Class<T> clazz, Predicate<T> filter) {
        super(new HashMap<>());

        this.clazz = clazz;
        this.filter = filter;
    }

    @Override
    protected List<PutArgumentDefinition<ScorePlayerTeam, T>> argumentDefinitionsForPut() {
        return ListUtil.of(new PutArgumentDefinition<>(keyArgumentDefinitionForPut(),
                                                       new ArgumentDefinition<>(new EnumArgument<>("name",
                                                                                                   clazz,
                                                                                                   opt -> opt.validator(
                                                                                                           filter)),
                                                                                (t, ctx) -> t)));
    }

    @Override
    protected String valueToString(T t) {
        return t.name();
    }
}
