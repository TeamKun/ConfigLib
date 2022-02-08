package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.Argument;
import dev.kotx.flylib.command.UsageBuilder;
import net.kunmc.lab.configlib.MapValue;
import net.kunmc.lab.configlib.argument.UnparsedArgument;
import net.kunmc.lab.configlib.util.CommandUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class UUID2ObjectMapValue<V> extends MapValue<UUID, V> {
    private transient boolean puttable = true;
    private transient boolean removable = true;
    private transient boolean clearable = true;
    private transient boolean listable = true;
    private transient boolean onlyOnline = true;

    public UUID2ObjectMapValue() {
        this(new HashMap<>());
    }

    public UUID2ObjectMapValue(Map<UUID, V> value) {
        super(value);
    }

    private Stream<OfflinePlayer> getPlayerStreamForPut() {
        return Arrays.stream(Bukkit.getOfflinePlayers())
                .filter(p -> !onlyOnline || p.isOnline());
    }

    @Override
    protected boolean puttableByCommand() {
        return puttable;
    }

    public <U extends UUID2ObjectMapValue<V>> U puttableByCommand(boolean puttable) {
        this.puttable = puttable;
        return ((U) this);
    }

    @Override
    protected void appendKeyArgumentForPut(UsageBuilder builder) {
        List<Argument<?>> arguments = CommandUtil.getArguments(builder);

        arguments.add(new UnparsedArgument("target", () -> {
            List<String> list = getPlayerStreamForPut()
                    .map(OfflinePlayer::getName)
                    .collect(Collectors.toList());
            if (!list.isEmpty()) {
                list.add("@r");
            }

            return list;
        }));
    }

    @Override
    protected boolean isCorrectKeyArgumentForPut(List<Object> argument, CommandSender sender) {
        String sel = argument.get(0).toString();
        return sel.equals("@r") ||
                getPlayerStreamForPut()
                        .map(OfflinePlayer::getName)
                        .anyMatch(s -> s.equals(sel));

    }

    @Override
    protected String incorrectKeyArgumentMessageForPut(List<Object> argument) {
        String s = argument.get(0).toString();

        if (s.startsWith("@")) {
            return "セレクターは@rのみ指定できます.";
        }

        return "プレイヤーが見つかりませんでした.";
    }

    @Override
    protected UUID argumentToKeyForPut(List<Object> argument, CommandSender sender) {
        String s = argument.get(0).toString();

        if (s.equals("@r")) {
            List<UUID> list = getPlayerStreamForPut()
                    .map(OfflinePlayer::getUniqueId)
                    .collect(Collectors.toList());
            Collections.shuffle(list);
            return list.get(0);
        }

        return Bukkit.getOfflinePlayerIfCached(s).getUniqueId();
    }

    @Override
    protected boolean removableByCommand() {
        return removable;
    }

    public <U extends UUID2ObjectMapValue<V>> U removableByCommand(boolean removable) {
        this.removable = removable;
        return ((U) this);
    }

    @Override
    protected void appendKeyArgumentForRemove(UsageBuilder builder) {
        List<Argument<?>> arguments = CommandUtil.getArguments(builder);

        arguments.add(new UnparsedArgument("target", () -> {
            List<String> list = value.keySet().stream()
                    .map(Bukkit::getOfflinePlayer)
                    .map(OfflinePlayer::getName)
                    .collect(Collectors.toList());
            if (!list.isEmpty()) {
                list.add("@r");
            }

            return list;
        }));
    }

    @Override
    protected boolean isCorrectKeyArgumentForRemove(List<Object> argument, CommandSender sender) {
        String sel = argument.get(0).toString();
        return sel.equals("@r") ||
                value.keySet().stream()
                        .map(Bukkit::getOfflinePlayer)
                        .map(OfflinePlayer::getName)
                        .anyMatch(s -> s.equals(sel));
    }

    @Override
    protected String incorrectKeyArgumentMessageForRemove(List<Object> argument) {
        String s = argument.get(0).toString();

        if (s.equals("@r")) {
            return "セレクターは@rのみ指定できます.";
        }

        return s + "は追加されていませんでした.";
    }

    @Override
    protected UUID argumentToKeyForRemove(List<Object> argument, CommandSender sender) {
        String s = argument.get(0).toString();

        if (s.equals("@r")) {
            List<UUID> list = new ArrayList<>(value.keySet());
            Collections.shuffle(list);
            return list.get(0);
        }

        return Bukkit.getOfflinePlayerIfCached(s).getUniqueId();
    }

    @Override
    protected boolean clearableByCommand() {
        return clearable;
    }

    public <U extends UUID2ObjectMapValue<V>> U clearableByCommand(boolean clearable) {
        this.clearable = clearable;
        return ((U) this);
    }

    @Override
    protected String keyToString(UUID uuid) {
        return Optional.ofNullable(Bukkit.getEntity(uuid))
                .map(Entity::getName)
                .orElse("null");
    }

    @Override
    protected boolean listable() {
        return listable;
    }

    public <U extends UUID2ObjectMapValue<V>> U listable(boolean listable) {
        this.listable = listable;
        return ((U) this);
    }

}
