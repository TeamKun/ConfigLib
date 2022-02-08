package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;
import dev.kotx.flylib.command.arguments.StringArgument;
import net.kunmc.lab.configlib.MapValue;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Team2ObjectMap<V> extends MapValue<Team, V> {
    private transient Scoreboard scoreboard;
    private transient boolean puttable = true;
    private transient boolean removable = true;
    private transient boolean clearable = true;
    private transient boolean listable = true;

    public Team2ObjectMap() {
        this(new HashMap<>());
    }

    public Team2ObjectMap(Map<Team, V> map) {
        super(map);

        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    }

    public @NotNull Scoreboard scoreboard() {
        return scoreboard;
    }

    public <U extends Team2ObjectMap<V>> U scoreboard(@NotNull Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
        return ((U) this);
    }

    @Override
    protected boolean puttableByCommand() {
        return puttable;
    }

    public <U extends Team2ObjectMap<V>> U puttableByCommand(boolean puttable) {
        this.puttable = puttable;
        return ((U) this);
    }

    @Override
    protected void appendKeyArgumentForPut(UsageBuilder builder) {
        builder.stringArgument("team", StringArgument.Type.PHRASE_QUOTED, suggestionBuilder -> {
            scoreboard.getTeams().stream()
                    .map(Team::getName)
                    .forEach(suggestionBuilder::suggest);
        });
    }

    @Override
    protected boolean isCorrectKeyArgumentForPut(List<Object> argument, CommandSender sender) {
        return scoreboard.getTeams().stream()
                .map(Team::getName)
                .anyMatch(s -> s.equals(argument.get(0)));
    }

    @Override
    protected String incorrectKeyArgumentMessageForPut(List<Object> argument) {
        return argument.get(0) + "は存在しないチームです.";
    }

    @Override
    protected Team argumentToKeyForPut(List<Object> argument, CommandSender sender) {
        return scoreboard.getTeam(argument.get(0).toString());
    }

    @Override
    protected boolean validateKeyForPut(Team team) {
        return true;
    }

    @Override
    protected String invalidKeyMessageForPut(String entryName, Team team) {
        return "This message can't be shown.";
    }

    @Override
    protected boolean removableByCommand() {
        return removable;
    }

    public <U extends Team2ObjectMap<V>> U removableByCommand(boolean removable) {
        this.removable = removable;
        return ((U) this);
    }

    @Override
    protected void appendKeyArgumentForRemove(UsageBuilder builder) {
        builder.stringArgument("team", StringArgument.Type.PHRASE_QUOTED, sb -> {
            value.keySet().stream()
                    .map(Team::getName)
                    .forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectKeyArgumentForRemove(List<Object> argument, CommandSender sender) {
        return scoreboard.getTeams().stream()
                .map(Team::getName)
                .anyMatch(s -> s.equals(argument.get(0)));
    }

    @Override
    protected String incorrectKeyArgumentMessageForRemove(List<Object> argument) {
        return argument.get(0) + "は存在しないチームです.";
    }

    @Override
    protected Team argumentToKeyForRemove(List<Object> argument, CommandSender sender) {
        return scoreboard.getTeam(argument.get(0).toString());
    }

    @Override
    protected boolean clearableByCommand() {
        return clearable;
    }

    public <U extends Team2ObjectMap<V>> U clearableByCommand(boolean clearable) {
        this.clearable = clearable;
        return ((U) this);
    }

    @Override
    protected String keyToString(Team team) {
        return team.getName();
    }

    @Override
    protected boolean listable() {
        return listable;
    }

    public <U extends Team2ObjectMap<V>> U listable(boolean listable) {
        this.listable = listable;
        return ((U) this);
    }
}
