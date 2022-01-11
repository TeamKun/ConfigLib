package net.kunmc.lab.configlib.value;

import com.google.common.collect.Sets;
import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * If you need completions on "add" with NewScoreboard, you should register it with scoreboard(Scoreboard) method.
 * When you use NewScoreboard, this class can't be deserialized. Therefore, a field must be qualified as transient at that time.
 */
public class TeamSetValue extends SetValue<Team> {
    private transient Scoreboard scoreboard;

    public TeamSetValue(Team... teams) {
        this(new HashSet<>(Arrays.asList(teams)));
    }

    public TeamSetValue(@NotNull Set<Team> value) {
        super(value);
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    }

    public @NotNull Scoreboard scoreboard() {
        return scoreboard;
    }

    public TeamSetValue scoreboard(@NotNull Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
        return this;
    }

    @Override
    public String invalidValueMessageForAdd(String entryName, Set<Team> element) {
        return element.toArray(new Team[0])[0].getName() + "はすでに" + entryName + "に追加されています.";
    }

    @Override
    public String succeedMessageForAdd(String entryName, Set<Team> element) {
        return entryName + "に" + element.toArray(new Team[0])[0].getName() + "を追加しました.";
    }

    @Override
    public String invalidValueMessageForRemove(String entryName, Set<Team> element) {
        return element.toArray(new Team[0])[0].getName() + "は" + entryName + "に追加されていませんでした.";
    }

    @Override
    public String succeedMessageForRemove(String entryName, Set<Team> element) {
        return entryName + "から" + element.toArray(new Team[0])[0].getName() + "を削除しました.";
    }

    @Override
    public String clearMessage(String entryName) {
        return entryName + "をクリアしました.";
    }

    @Override
    public void appendArgumentForAdd(UsageBuilder builder) {
        builder.textArgument("TeamName", suggestionBuilder -> {
            scoreboard.getTeams().stream()
                    .map(Team::getName)
                    .filter(name -> value.stream()
                            .map(Team::getName)
                            .noneMatch(s -> s.equals(name)))
                    .forEach(suggestionBuilder::suggest);
        });
    }

    @Override
    public void appendArgumentForRemove(UsageBuilder builder) {
        builder.textArgument("TeamName", suggestionBuilder -> {
            value.stream()
                    .map(Team::getName)
                    .forEach(suggestionBuilder::suggest);
        });
    }

    @Override
    public boolean isCorrectArgumentForAdd(Object argument, CommandSender sender) {
        return scoreboard.getTeams().stream()
                .filter(t -> !value.contains(t))
                .map(Team::getName)
                .anyMatch(s -> s.equals(argument));
    }

    @Override
    public boolean isCorrectArgumentForRemove(Object argument, CommandSender sender) {
        return value.stream()
                .map(Team::getName)
                .anyMatch(s -> s.equals(argument));
    }

    @Override
    public Set<Team> argumentToValueForAdd(Object argument, CommandSender sender) {
        Team t = scoreboard.getTeam(argument.toString());
        return Sets.newHashSet(t);
    }

    @Override
    public Set<Team> argumentToValueForRemove(Object argument, CommandSender sender) {
        Team t = scoreboard.getTeam(argument.toString());
        return Sets.newHashSet(t);
    }

    @Override
    public void sendListMessage(CommandContext ctx, String entryName) {
        String header = "-----" + entryName + "-----";
        ctx.message(ChatColor.YELLOW + header);

        ctx.success(this.stream()
                .map(Team::getName)
                .collect(Collectors.joining(",")));

        ctx.message(ChatColor.YELLOW + StringUtils.repeat("-", header.length()));
    }
}
