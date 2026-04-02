package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.exception.CommandPrerequisiteException;
import net.kunmc.lab.configlib.exception.InvalidValueException;
import net.kunmc.lab.configlib.util.function.ExecutionCondition;
import net.kunmc.lab.configlib.util.function.Validator;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class Value<E, T extends Value<E, T>> {
    private transient final List<Consumer<E>> initializeListeners = new ArrayList<>();
    private transient final List<Consumer<E>> modifyListeners = new ArrayList<>();
    protected E value;
    private String description;
    private transient boolean listable = true;
    private transient volatile boolean initialized = false;
    private transient E initValue;
    private transient Validator<E> validator = x -> {
    };
    private transient Function<E, String> formatter;
    private transient String entryName;
    private transient ExecutionCondition executableIf;

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

    /**
     * Sets a custom entry name used when registering this value's command.
     * By default, the field name is used.
     */
    public final T entryName(String entryName) {
        this.entryName = entryName;
        return ((T) this);
    }

    /**
     * Sets a custom formatter for converting this value to a display string.
     * The formatter takes precedence over the default {@link #asString()} implementation
     * and is used wherever the value is displayed as text (e.g. list and get commands).
     *
     * <pre>{@code
     * new IntegerValue(10).formatter(n -> n + " items")
     * }</pre>
     */
    public final T formatter(Function<@Nullable E, String> formatter) {
        this.formatter = formatter;
        return (T) this;
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
        return onModify(listener, false);
    }

    /**
     * Adds a listener triggered on value modified.
     * If triggeredOnInitialize is true, The listener also will be triggered after the value has been initialized.
     *
     * @see Value#onInitialize(Consumer)
     */
    public final T onModify(Consumer<E> listener, boolean triggeredOnInitialize) {
        modifyListeners.add(listener);
        if (triggeredOnInitialize) {
            onInitialize(listener);
        }

        return ((T) this);
    }

    final void onModifyValue(E newValue) {
        modifyListeners.forEach(x -> x.accept(newValue));
    }

    /**
     * Validates values on executing modify command and loading config.<br>
     * Throwing {@link net.kunmc.lab.configlib.exception.InvalidValueException}, you can customize the error message.
     */
    public final T addValidator(Validator<E> validator) {
        this.validator = this.validator.and(validator);
        return ((T) this);
    }

    final void validate(E value) throws InvalidValueException {
        validator.validate(value);
    }

    final String resolveEntryName(String fieldName) {
        if (entryName != null) {
            return entryName;
        }
        return fieldName;
    }

    /**
     * Sets a condition for this value's command execution.
     * Throw {@link net.kunmc.lab.commandlib.exception.CommandPrerequisiteException} to deny execution.
     *
     * <pre>{@code
     * new IntegerValue(10).executableIf(ctx -> {
     *     if (!ctx.getSender().isOp()) throw new CommandPrerequisiteException(c -> c.sendFailure("Ops only."));
     * })
     * }</pre>
     */
    public final T executableIf(ExecutionCondition condition) {
        this.executableIf = condition;
        return (T) this;
    }

    /**
     * Sets a simple boolean condition. Uses a default error message when denied.
     */
    public final T executableIf(Predicate<CommandContext> condition) {
        return executableIf(ctx -> {
            if (!condition.test(ctx)) {
                throw new CommandPrerequisiteException(c -> c.sendFailure("このコマンドを実行できません"));
            }
        });
    }

    final void checkExecutable(CommandContext ctx) throws CommandPrerequisiteException {
        if (executableIf != null) {
            executableIf.check(ctx);
        }
    }

    final String format() {
        if (formatter != null) {
            return formatter.apply(value);
        }
        return asString();
    }

    protected abstract String asString();
}
