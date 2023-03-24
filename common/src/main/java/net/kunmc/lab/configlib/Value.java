package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.CommandContext;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class Value<E, T extends Value<E, T>> {
    protected E value;
    private String description;
    private transient boolean listable = true;
    private transient volatile boolean initialized = false;
    private transient E initValue;
    private transient final List<Consumer<E>> initializeListeners = new ArrayList<>();
    private transient final List<Consumer<E>> modifyListeners = new ArrayList<>();

    public Value(E value) {
        this.value = value;
    }

    public E value() {
        return value;
    }

    public void value(E value) {
        this.value = value;
    }

    @Nullable
    public final String description() {
        return description;
    }

    public final T description(String description) {
        this.description = description;
        return ((T) this);
    }

    protected final boolean listable() {
        return listable;
    }

    public final T listable(boolean listable) {
        this.listable = listable;
        return ((T) this);
    }

    final int valueHashCode() {
        if (value == null) {
            return 0;
        }
        return value.hashCode();
    }

    /**
     * Adds a listener that will be triggered after the value has been initialized.
     * If initialization has already been completed, the listener will be immediately triggered.
     */
    public final T onInitialize(Consumer<E> listener) {
        if (initialized) {
            listener.accept(initValue);
            return ((T) this);
        }

        initializeListeners.add(listener);
        return ((T) this);
    }

    final void onInitializeValue(E initValue) {
        initializeListeners.forEach(x -> x.accept(initValue));
        this.initialized = true;
        this.initValue = initValue;
    }

    /**
     * Adds a listener triggered on value modified.
     */
    public final T onModify(Consumer<E> listener) {
        modifyListeners.add(listener);
        return ((T) this);
    }

    final void onModifyValue(E newValue) {
        modifyListeners.forEach(x -> x.accept(newValue));
    }

    protected abstract String asString(CommandContext ctx);
}
