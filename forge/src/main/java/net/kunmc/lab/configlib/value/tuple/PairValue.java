package net.kunmc.lab.configlib.value.tuple;

import net.kunmc.lab.configlib.SingleValue;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

public abstract class PairValue<L, R, T extends PairValue<L, R, T>> extends SingleValue<Pair<L, R>, T> {
    public PairValue(L left, R right) {
        this(MutablePair.of(left, right));
    }

    private PairValue(Pair<L, R> value) {
        super(value);
    }

    @Override
    protected String valueToString(Pair<L, R> pair) {
        return String.format("(%s, %s)", pair.getLeft(), pair.getRight());
    }

    protected abstract String leftToString(L left);

    protected abstract String rightToString(R right);
}
