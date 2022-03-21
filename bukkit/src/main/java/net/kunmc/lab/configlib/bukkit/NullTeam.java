package net.kunmc.lab.configlib.bukkit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class NullTeam implements Team {
    @Override
    public @NotNull String getName() throws IllegalStateException {
        return "null";
    }

    @Override
    public @NotNull Component displayName() throws IllegalStateException {
        return Component.text("null");
    }

    @Override
    public void displayName(@Nullable Component component) throws IllegalStateException, IllegalArgumentException {

    }

    @Override
    public @NotNull Component prefix() throws IllegalStateException {
        return Component.text("");
    }

    @Override
    public void prefix(@Nullable Component component) throws IllegalStateException, IllegalArgumentException {

    }

    @Override
    public @NotNull Component suffix() throws IllegalStateException {
        return Component.text("");
    }

    @Override
    public void suffix(@Nullable Component component) throws IllegalStateException, IllegalArgumentException {

    }

    @Override
    public @NotNull TextColor color() throws IllegalStateException {
        return TextColor.fromHexString("#FFFFFF");
    }

    @Override
    public void color(@Nullable NamedTextColor namedTextColor) {

    }

    @Override
    public @NotNull String getDisplayName() throws IllegalStateException {
        return "";
    }

    @Override
    public void setDisplayName(@NotNull String s) throws IllegalStateException, IllegalArgumentException {

    }

    @Override
    public @NotNull String getPrefix() throws IllegalStateException {
        return "";
    }

    @Override
    public void setPrefix(@NotNull String s) throws IllegalStateException, IllegalArgumentException {

    }

    @Override
    public @NotNull String getSuffix() throws IllegalStateException {
        return "";
    }

    @Override
    public void setSuffix(@NotNull String s) throws IllegalStateException, IllegalArgumentException {

    }

    @Override
    public @NotNull ChatColor getColor() throws IllegalStateException {
        return ChatColor.WHITE;
    }

    @Override
    public void setColor(@NotNull ChatColor chatColor) {

    }

    @Override
    public boolean allowFriendlyFire() throws IllegalStateException {
        return false;
    }

    @Override
    public void setAllowFriendlyFire(boolean b) throws IllegalStateException {

    }

    @Override
    public boolean canSeeFriendlyInvisibles() throws IllegalStateException {
        return false;
    }

    @Override
    public void setCanSeeFriendlyInvisibles(boolean b) throws IllegalStateException {

    }

    @Override
    public @NotNull NameTagVisibility getNameTagVisibility() throws IllegalArgumentException {
        return NameTagVisibility.ALWAYS;
    }

    @Override
    public void setNameTagVisibility(@NotNull NameTagVisibility nameTagVisibility) throws IllegalArgumentException {

    }

    @Override
    public @NotNull Set<OfflinePlayer> getPlayers() throws IllegalStateException {
        return new HashSet<>();
    }

    @Override
    public @NotNull Set<String> getEntries() throws IllegalStateException {
        return new HashSet<>();
    }

    @Override
    public int getSize() throws IllegalStateException {
        return 0;
    }

    @Override
    public @Nullable Scoreboard getScoreboard() {
        return null;
    }

    @Override
    public void addPlayer(@NotNull OfflinePlayer offlinePlayer) throws IllegalStateException, IllegalArgumentException {

    }

    @Override
    public void addEntry(@NotNull String s) throws IllegalStateException, IllegalArgumentException {

    }

    @Override
    public boolean removePlayer(@NotNull OfflinePlayer offlinePlayer) throws IllegalStateException, IllegalArgumentException {
        return false;
    }

    @Override
    public boolean removeEntry(@NotNull String s) throws IllegalStateException, IllegalArgumentException {
        return false;
    }

    @Override
    public void unregister() throws IllegalStateException {

    }

    @Override
    public boolean hasPlayer(@NotNull OfflinePlayer offlinePlayer) throws IllegalArgumentException, IllegalStateException {
        return false;
    }

    @Override
    public boolean hasEntry(@NotNull String s) throws IllegalArgumentException, IllegalStateException {
        return false;
    }

    @Override
    public @NotNull OptionStatus getOption(@NotNull Option option) throws IllegalStateException {
        return OptionStatus.ALWAYS;
    }

    @Override
    public void setOption(@NotNull Option option, @NotNull OptionStatus optionStatus) throws IllegalStateException {

    }
}
