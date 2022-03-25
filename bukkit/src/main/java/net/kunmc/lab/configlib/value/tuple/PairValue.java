package net.kunmc.lab.configlib.value.tuple;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.configlib.SingleValue;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.command.CommandSender;

import java.util.List;

import static org.bukkit.ChatColor.RED;

public abstract class PairValue<L, R, T extends PairValue<L, R, T>> extends SingleValue<Pair<L, R>, T> {
    public PairValue(L left, R right) {
        this(MutablePair.of(left, right));
    }

    private PairValue(Pair<L, R> value) {
        super(value);
    }

    @Override
    protected void appendArgument(ArgumentBuilder builder) {
        appendLeftArgument(builder);
        appendRightArgument(builder);
    }

    protected abstract void appendLeftArgument(ArgumentBuilder builder);

    protected abstract void appendRightArgument(ArgumentBuilder builder);

    @Override
    protected boolean isCorrectArgument(String entryName, List<Object> argument, CommandSender sender) {
        boolean left = isCorrectLeftArgument(entryName, argument, sender);
        if (!left) {
            sender.sendMessage(RED + incorrectLeftArgumentMessage(entryName, argument, sender));
        }

        boolean right = isCorrectRightArgument(entryName, argument, sender);
        if (!right) {
            sender.sendMessage(RED + incorrectRightArgumentMessage(entryName, argument, sender));
        }

        return left && right;
    }

    protected abstract boolean isCorrectLeftArgument(String entryName, List<Object> argument, CommandSender sender);

    protected abstract boolean isCorrectRightArgument(String entryName, List<Object> argument, CommandSender sender);

    @Override
    protected String incorrectArgumentMessage(String entryName, List<Object> argument, CommandSender sender) {
        return "";
    }

    protected abstract String incorrectLeftArgumentMessage(String entryName, List<Object> argument, CommandSender sender);

    protected abstract String incorrectRightArgumentMessage(String entryName, List<Object> argument, CommandSender sender);

    @Override
    protected Pair<L, R> argumentToValue(List<Object> argument, CommandSender sender) {
        return MutablePair.of(argumentToLeftValue(argument, sender), argumentToRightValue(argument, sender));
    }

    protected abstract L argumentToLeftValue(List<Object> argument, CommandSender sender);

    protected abstract R argumentToRightValue(List<Object> argument, CommandSender sender);

    @Override
    protected boolean validateOnSet(String entryName, Pair<L, R> newValue, CommandSender sender) {
        boolean left = validateLeft(entryName, newValue.getLeft(), sender);
        if (!left) {
            sender.sendMessage(RED + invalidLeftValueMessage(entryName, newValue.getLeft(), sender));
        }

        boolean right = validateRight(entryName, newValue.getRight(), sender);
        if (!right) {
            sender.sendMessage(RED + invalidRightValueMessage(entryName, newValue.getRight(), sender));
        }

        return left && right;
    }

    protected boolean validateLeft(String entryName, L newLeft, CommandSender sender) {
        return true;
    }

    protected boolean validateRight(String entryName, R newRight, CommandSender sender) {
        return true;
    }

    @Override
    protected String invalidValueMessage(String entryName, Pair<L, R> newValue, CommandSender sender) {
        return "";
    }

    protected String invalidLeftValueMessage(String entryName, L newLeft, CommandSender sender) {
        return "";
    }

    protected String invalidRightValueMessage(String entryName, R newRight, CommandSender sender) {
        return "";
    }

    @Override
    protected String valueToString(Pair<L, R> pair) {
        return String.format("(%s, %s)", pair.getLeft(), pair.getRight());
    }

    protected abstract String leftToString(L left);

    protected abstract String rightToString(R right);
}
