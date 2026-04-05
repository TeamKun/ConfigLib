package net.kunmc.lab.configlib;

import net.kunmc.lab.configlib.command.CollectionValueAddCommandMessageParameter;
import net.kunmc.lab.configlib.command.CollectionValueClearCommandMessageParameter;
import net.kunmc.lab.configlib.command.CollectionValueRemoveCommandMessageParameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class CollectionValue<T extends Collection<E>, E, U extends CollectionValue<T, E, U>> extends Value<T, U> {
    private final transient List<Consumer<T>> addListeners = new ArrayList<>();
    private final transient List<Consumer<T>> removeListeners = new ArrayList<>();
    private final transient List<Runnable> clearListeners = new ArrayList<>();
    private transient boolean addable = true;
    private transient boolean removable = true;
    private transient boolean clearable = true;
    private transient Function<CollectionValueAddCommandMessageParameter<T>, String> successMessageForAdd;
    private transient Function<CollectionValueRemoveCommandMessageParameter<T>, String> successMessageForRemove;
    private transient Function<CollectionValueClearCommandMessageParameter, String> successMessageForClear;

    public CollectionValue(T value) {
        super(value);
    }

    public final U addableByCommand(boolean addable) {
        this.addable = addable;
        return ((U) this);
    }

    protected final boolean addableByCommand() {
        return addable;
    }

    protected abstract List<ArgumentDefinition<T>> argumentDefinitionsForAdd();

    /**
     * Add a listener fired on add command.
     */
    public final U onAdd(Consumer<T> listener) {
        addListeners.add(listener);
        return ((U) this);
    }

    protected final void dispatchAdd(T newValue) {
        addListeners.forEach(x -> x.accept(newValue));
    }

    /**
     * Sets a custom success message shown after an element is added via command.
     */
    public final U successMessageForAdd(Function<CollectionValueAddCommandMessageParameter<T>, String> successMessage) {
        this.successMessageForAdd = successMessage;
        return (U) this;
    }

    protected String succeedMessageForAdd(CollectionValueAddCommandMessageParameter<T> param) {
        if (successMessageForAdd != null) {
            return successMessageForAdd.apply(param);
        }
        return String.format("%sに%sを追加しました.",
                             param.entryName(),
                             elementToString(((E[]) param.added()
                                                         .toArray())[0]));
    }

    public final U removableByCommand(boolean removable) {
        this.removable = removable;
        return ((U) this);
    }

    protected final boolean removableByCommand() {
        return removable;
    }

    protected abstract List<ArgumentDefinition<T>> argumentDefinitionsForRemove();

    /**
     * Add a listener fired on remove command.
     */
    public final U onRemove(Consumer<T> listener) {
        removeListeners.add(listener);
        return ((U) this);
    }

    final void dispatchRemove(T newValue) {
        removeListeners.forEach(x -> x.accept(newValue));
    }

    /**
     * Sets a custom success message shown after an element is removed via command.
     */
    public final U successMessageForRemove(Function<CollectionValueRemoveCommandMessageParameter<T>, String> successMessage) {
        this.successMessageForRemove = successMessage;
        return (U) this;
    }

    protected String succeedMessageForRemove(CollectionValueRemoveCommandMessageParameter<T> param) {
        if (successMessageForRemove != null) {
            return successMessageForRemove.apply(param);
        }
        return String.format("%sから%sを削除しました.",
                             param.entryName(),
                             elementToString(((E[]) param.removed()
                                                         .toArray())[0]));
    }

    public final U clearableByCommand(boolean clearable) {
        this.clearable = clearable;
        return ((U) this);
    }

    protected final boolean clearableByCommand() {
        return clearable;
    }

    /**
     * Add a listener fired on clear command.
     */
    public final U onClear(Runnable listener) {
        clearListeners.add(listener);
        return ((U) this);
    }

    final void dispatchClear() {
        clearListeners.forEach(Runnable::run);
    }

    /**
     * Sets a custom success message shown after the collection is cleared via command.
     */
    public final U successMessageForClear(Function<CollectionValueClearCommandMessageParameter, String> successMessage) {
        this.successMessageForClear = successMessage;
        return (U) this;
    }

    protected String succeedMessageForClear(CollectionValueClearCommandMessageParameter param) {
        if (successMessageForClear != null) {
            return successMessageForClear.apply(param);
        }
        return param.entryName() + "をクリアしました";
    }

    public abstract T toAdded(E... elements);

    public abstract T toRemoved(E... elements);

    protected abstract String elementToString(E e);

    @Override
    protected String asString() {
        return "[" + value.stream()
                          .map(this::elementToString)
                          .collect(Collectors.joining(", ")) + "]";
    }
}
