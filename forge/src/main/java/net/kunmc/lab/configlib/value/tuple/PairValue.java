package net.kunmc.lab.configlib.value.tuple;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.configlib.SingleValue;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

import static net.minecraft.util.text.TextFormatting.RED;

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
    protected boolean isCorrectArgument(String entryName, List<Object> argument, CommandSource sender) {
        boolean left = isCorrectLeftArgument(entryName, argument, sender);
        if (!left) {
            sender.sendFeedback(new StringTextComponent(RED + incorrectLeftArgumentMessage(entryName, argument, sender)), false);
        }

        boolean right = isCorrectRightArgument(entryName, argument, sender);
        if (!right) {
            sender.sendFeedback(new StringTextComponent(RED + incorrectRightArgumentMessage(entryName, argument, sender)), false);
        }

        return left && right;
    }

    protected abstract boolean isCorrectLeftArgument(String entryName, List<Object> argument, CommandSource sender);

    protected abstract boolean isCorrectRightArgument(String entryName, List<Object> argument, CommandSource sender);

    @Override
    protected String incorrectArgumentMessage(String entryName, List<Object> argument, CommandSource sender) {
        return "";
    }

    protected abstract String incorrectLeftArgumentMessage(String entryName, List<Object> argument, CommandSource sender);

    protected abstract String incorrectRightArgumentMessage(String entryName, List<Object> argument, CommandSource sender);

    @Override
    protected Pair<L, R> argumentToValue(List<Object> argument, CommandSource sender) {
        return MutablePair.of(argumentToLeftValue(argument, sender), argumentToRightValue(argument, sender));
    }

    protected abstract L argumentToLeftValue(List<Object> argument, CommandSource sender);

    protected abstract R argumentToRightValue(List<Object> argument, CommandSource sender);

    @Override
    protected boolean validateOnSet(String entryName, Pair<L, R> newValue, CommandSource sender) {
        boolean left = validateLeft(entryName, newValue.getLeft(), sender);
        if (!left) {
            sender.sendFeedback(new StringTextComponent(RED + invalidLeftValueMessage(entryName, newValue.getLeft(), sender)), false);
        }

        boolean right = validateRight(entryName, newValue.getRight(), sender);
        if (!right) {
            sender.sendFeedback(new StringTextComponent(RED + invalidRightValueMessage(entryName, newValue.getRight(), sender)), false);
        }

        return left && right;
    }

    protected boolean validateLeft(String entryName, L newLeft, CommandSource sender) {
        return true;
    }

    protected boolean validateRight(String entryName, R newRight, CommandSource sender) {
        return true;
    }

    @Override
    protected String invalidValueMessage(String entryName, Pair<L, R> newValue, CommandSource sender) {
        return "";
    }

    protected String invalidLeftValueMessage(String entryName, L newLeft, CommandSource sender) {
        return "";
    }

    protected String invalidRightValueMessage(String entryName, R newRight, CommandSource sender) {
        return "";
    }

    @Override
    protected String valueToString(Pair<L, R> pair) {
        return String.format("(%s, %s)", pair.getLeft(), pair.getRight());
    }

    protected abstract String leftToString(L left);

    protected abstract String rightToString(R right);
}
