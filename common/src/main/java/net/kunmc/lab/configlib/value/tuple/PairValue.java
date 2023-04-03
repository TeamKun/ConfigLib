package net.kunmc.lab.configlib.value.tuple;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.SingleValue;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.function.Function;

public abstract class PairValue<L, R, T extends PairValue<L, R, T>> extends SingleValue<MutablePair<L, R>, T> {
    private transient Function<Pair<L, R>, Boolean> validator = pair -> true;
    private transient Function<Pair<L, R>, String> invalidMessageSupplier = pair -> "引数の値が不正です.";

    public PairValue(L left, R right) {
        this(MutablePair.of(left, right));
    }

    private PairValue(MutablePair<L, R> value) {
        super(value);
    }

    public L getLeft() {
        return value.getLeft();
    }

    public void setLeft(L left) {
        value.setLeft(left);
    }

    public R getRight() {
        return value.getRight();
    }

    public void setRight(R right) {
        value.setRight(right);
    }

    public T setValidator(Function<Pair<L, R>, Boolean> validator) {
        this.validator = validator;
        return ((T) this);
    }

    public T setValidator(Function<Pair<L, R>, Boolean> validator,
                          Function<Pair<L, R>, String> invalidMessageSupplier) {
        this.validator = validator;
        this.invalidMessageSupplier = invalidMessageSupplier;
        return ((T) this);
    }

    @Override
    protected void appendArgument(ArgumentBuilder builder) {
        appendLeftArgument(builder);
        appendRightArgument(builder);
    }

    protected abstract void appendLeftArgument(ArgumentBuilder builder);

    protected abstract void appendRightArgument(ArgumentBuilder builder);

    @Override
    protected boolean isCorrectArgument(String entryName, List<Object> argument, CommandContext ctx) {
        boolean left = isCorrectLeftArgument(entryName, argument, ctx);
        if (!left) {
            ctx.sendFailure(incorrectLeftArgumentMessage(entryName, argument, ctx));
        }

        boolean right = isCorrectRightArgument(entryName, argument, ctx);
        if (!right) {
            ctx.sendFailure(incorrectRightArgumentMessage(entryName, argument, ctx));
        }

        return left && right;
    }

    protected boolean isCorrectLeftArgument(String entryName, List<Object> argument, CommandContext ctx) {
        return true;
    }

    protected boolean isCorrectRightArgument(String entryName, List<Object> argument, CommandContext ctx) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessage(String entryName, List<Object> argument, CommandContext ctx) {
        return "";
    }

    protected String incorrectLeftArgumentMessage(String entryName, List<Object> argument, CommandContext ctx) {
        return "";
    }

    protected String incorrectRightArgumentMessage(String entryName, List<Object> argument, CommandContext ctx) {
        return "";
    }

    @Override
    protected MutablePair<L, R> argumentToValue(List<Object> argument, CommandContext ctx) {
        return MutablePair.of(argumentToLeftValue(argument, ctx), argumentToRightValue(argument, ctx));
    }

    protected abstract L argumentToLeftValue(List<Object> argument, CommandContext ctx);

    protected abstract R argumentToRightValue(List<Object> argument, CommandContext ctx);

    @Override
    protected boolean validateOnSet(String entryName, MutablePair<L, R> newValue, CommandContext ctx) {
        if (!validator.apply(newValue)) {
            ctx.sendFailure(invalidMessageSupplier.apply(newValue));
            return false;
        }

        boolean left = validateLeft(entryName, newValue.getLeft(), ctx, newValue);
        if (!left) {
            ctx.sendFailure(invalidLeftValueMessage(entryName, newValue.getLeft(), ctx));
        }

        boolean right = validateRight(entryName, newValue.getRight(), ctx, newValue);
        if (!right) {
            ctx.sendFailure(invalidRightValueMessage(entryName, newValue.getRight(), ctx));
        }

        return left && right;
    }

    protected boolean validateLeft(String entryName, L newLeft, CommandContext ctx, Pair<L, R> newPair) {
        return true;
    }

    protected boolean validateRight(String entryName, R newRight, CommandContext ctx, Pair<L, R> newPair) {
        return true;
    }

    @Override
    protected String invalidValueMessage(String entryName, MutablePair<L, R> newValue, CommandContext ctx) {
        return "";
    }

    protected String invalidLeftValueMessage(String entryName, L newLeft, CommandContext ctx) {
        return "";
    }

    protected String invalidRightValueMessage(String entryName, R newRight, CommandContext ctx) {
        return "";
    }

    @Override
    protected String valueToString(MutablePair<L, R> pair) {
        String leftName = "null";
        if (getLeft() != null) {
            leftName = leftToString(getLeft());
        }

        String rightName = "null";
        if (getRight() != null) {
            rightName = rightToString(getRight());
        }

        return String.format("(%s, %s)", leftName, rightName);
    }

    protected abstract String leftToString(L left);

    protected abstract String rightToString(R right);
}
