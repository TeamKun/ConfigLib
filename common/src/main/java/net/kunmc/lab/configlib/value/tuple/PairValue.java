package net.kunmc.lab.configlib.value.tuple;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.SingleValue;
import net.kunmc.lab.configlib.util.function.ArgumentApplier;
import net.kunmc.lab.configlib.util.function.ArgumentMapper;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class PairValue<L, R, T extends PairValue<L, R, T>> extends SingleValue<MutablePair<L, R>, T> {
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

    /**
     * Defines how arguments are applied to a builder and mapped to a value.
     * The mapper may throw {@link net.kunmc.lab.commandlib.exception.InvalidArgumentException}
     * to send an error message to the command executor.
     */
    public static class PairArgumentDefinition<L, R> implements ArgumentApplier, ArgumentMapper<MutablePair<L, R>> {
        private final ArgumentDefinition<L> left;
        private final ArgumentDefinition<R> right;
        private Consumer<Pair<L, R>> validator = (p) -> {
        };

        public PairArgumentDefinition(ArgumentDefinition<L> left, ArgumentDefinition<R> right) {
            this.left = left;
            this.right = right;
        }

        public PairArgumentDefinition<L, R> validator(Consumer<Pair<L, R>> validator) {
            this.validator = Objects.requireNonNull(validator);
            return this;
        }

        @Override
        public void applyArgument(ArgumentBuilder builder) {
            left.applyArgument(builder);
            right.applyArgument(builder);
        }

        @Override
        public MutablePair<L, R> mapArgument(CommandContext ctx) {
            MutablePair<L, R> value = MutablePair.of(left.mapArgument(ctx), right.mapArgument(ctx));
            validator.accept(value);
            return value;
        }
    }
}
