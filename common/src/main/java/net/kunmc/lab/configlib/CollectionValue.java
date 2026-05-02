package net.kunmc.lab.configlib;

import net.kunmc.lab.configlib.command.CollectionValueAddCommandMessageParameter;
import net.kunmc.lab.configlib.command.CollectionValueClearCommandMessageParameter;
import net.kunmc.lab.configlib.command.CollectionValueRemoveCommandMessageParameter;
import org.jetbrains.annotations.Nullable;

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

    public final U disableAdd() {
        this.addable = false;
        return ((U) this);
    }

    protected final boolean isAddEnabled() {
        return addable;
    }

    protected abstract List<ArgumentDefinition<T>> argumentDefinitionsForAdd();

    /**
     * Adds a listener fired only after an element is added by a generated command.
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
        return param.describe(ConfigCommandDescriptions.Key.COLLECTION_ADD_SUCCESS,
                              param.entryName(),
                              elementToString(((E[]) param.added()
                                                          .toArray())[0]));
    }

    public final U disableRemove() {
        this.removable = false;
        return ((U) this);
    }

    protected final boolean isRemoveEnabled() {
        return removable;
    }

    protected abstract List<ArgumentDefinition<T>> argumentDefinitionsForRemove();

    /**
     * Adds a listener fired only after an element is removed by a generated command.
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
        return param.describe(ConfigCommandDescriptions.Key.COLLECTION_REMOVE_SUCCESS,
                              param.entryName(),
                              elementToString(((E[]) param.removed()
                                                          .toArray())[0]));
    }

    public final U disableClear() {
        this.clearable = false;
        return ((U) this);
    }

    protected final boolean isClearEnabled() {
        return clearable;
    }

    /**
     * Adds a listener fired only after this collection is cleared by a generated command.
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
        return param.describe(ConfigCommandDescriptions.Key.COLLECTION_CLEAR_SUCCESS, param.entryName());
    }

    public abstract T toAdded(E... elements);

    public abstract T toRemoved(E... elements);

    protected abstract String elementToString(E e);

    @Override
    protected String defaultDisplayString(@Nullable T t) {
        if (t == null) {
            return "null";
        }

        return "[" + t.stream()
                      .map(this::elementToString)
                      .collect(Collectors.joining(", ")) + "]";
    }
}
