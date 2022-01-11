package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

/**
 * If you need completions with NewScoreboard, you should register it with scoreboard(Scoreboard) method.
 * When you use NewScoreboard, this class can't be deserialized. Therefore, a field must be qualified as transient at that time.
 */
public class TeamValue implements SingleValue<Team> {
    private Team value;
    private transient final Consumer<Team> consumer;
    private transient Scoreboard scoreboard;
    private transient boolean listable = true;
    private transient boolean writable = true;

    public TeamValue() {
        this((Team) null);
    }

    public TeamValue(Team team) {
        this(team, t -> {
        });
    }

    public TeamValue(Consumer<Team> onSet) {
        this(null, onSet);
    }

    public TeamValue(Team team, Consumer<Team> onSet) {
        value = team;
        consumer = onSet;
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    }

    public @NotNull Scoreboard scoreboard() {
        return scoreboard;
    }

    public TeamValue scoreboard(@NotNull Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
        return this;
    }

    @Override
    public boolean validateOnSet(Team newValue) {
        return !value.getName().equals(newValue.getName());
    }

    @Override
    public void onSetValue(Team newValue) {
        consumer.accept(newValue);
    }

    public TeamValue writableByCommand(boolean writable) {
        this.writable = writable;
        return this;
    }

    @Override
    public boolean writableByCommand() {
        return writable;
    }

    @Override
    public void appendArgument(UsageBuilder builder) {
        builder.textArgument("TeamName", suggestionBuilder -> {
            scoreboard.getTeams().stream()
                    .map(Team::getName)
                    .filter(s -> value == null || !s.equals(value.getName()))
                    .forEach(suggestionBuilder::suggest);
        });
    }

    @Override
    public boolean isCorrectArgument(List<Object> argument, CommandSender sender) {
        return scoreboard.getTeams().stream()
                .map(Team::getName)
                .anyMatch(s -> s.equals(argument.get(0)));
    }

    @Override
    public Team argumentToValue(List<Object> argument, CommandSender sender) {
        return scoreboard.getTeam(argument.get(0).toString());
    }

    @Override
    public Team value() {
        return value;
    }

    @Override
    public void value(Team value) {
        this.value = value;
    }

    public TeamValue listable(boolean listable) {
        this.listable = listable;
        return this;
    }

    @Override
    public boolean listable() {
        return listable;
    }

    @Override
    public String incorrectArgumentMessage(List<Object> argument) {
        return argument.get(0) + "は存在しないチームです.";
    }

    @Override
    public String invalidValueMessage(String entryName, Team newValue) {
        return newValue.getName() + "はすでに設定されているチームです.";
    }

    @Override
    public String succeedSetMessage(String entryName) {
        return entryName + "の値を" + value.getName() + "に設定しました.";
    }

    @Override
    public void sendListMessage(CommandContext ctx, String entryName) {
        if (value == null) {
            ctx.success(entryName + ": null");
        } else {
            ctx.success(entryName + ": " + value.getName());
        }
    }
}
