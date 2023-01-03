package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.SuggestionAction;
import net.kunmc.lab.commandlib.argument.StringArgument;
import net.kunmc.lab.configlib.SingleValue;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StringValue extends SingleValue<String, StringValue> {
    protected final transient List<String> allowableStringList = new ArrayList<>();
    private final int min;
    private final int max;
    protected transient String name = "string";
    protected transient StringArgument.Type type = StringArgument.Type.PHRASE;
    protected transient SuggestionAction suggestionAction = sb -> {
        allowableStringList.forEach(sb::suggest);
    };

    public StringValue(String value) {
        this(value, 0, 256);
    }

    public StringValue(String value, int min, int max) {
        super(value);
        this.min = min;
        this.max = max;
    }

    public StringValue name(@NotNull String name) {
        this.name = name;
        return this;
    }

    public StringValue type(@NotNull StringArgument.Type type) {
        this.type = type;
        return this;
    }

    public StringValue addAllowableString(@NotNull String s) {
        allowableStringList.add(s);
        return this;
    }

    public StringValue suggestionAction(@NotNull SuggestionAction action) {
        this.suggestionAction = action;
        return this;
    }

    @Override
    protected void appendArgument(ArgumentBuilder builder) {
        builder.stringArgument(name, type, suggestionAction);
    }

    @Override
    protected boolean isCorrectArgument(String entryName, List<Object> argument, CommandContext ctx) {
        if (allowableStringList.isEmpty()) {
            return true;
        }

        return allowableStringList.stream()
                                  .anyMatch(s -> s.equals(argument.get(0)));
    }

    @Override
    protected String incorrectArgumentMessage(String entryName, List<Object> argument, CommandContext ctx) {
        return argument.get(0) + "は不正な引数です.";
    }

    @Override
    protected String argumentToValue(List<Object> argument, CommandContext ctx) {
        return argument.get(0)
                       .toString();
    }

    @Override
    protected boolean validateOnSet(String entryName, String newValue, CommandContext ctx) {
        return newValue.length() >= min && newValue.length() <= max;
    }

    @Override
    protected String invalidValueMessage(String entryName, String argument, CommandContext ctx) {
        return entryName + "は" + min + "以上" + max + "以下の文字数で入力してください";
    }

    @Override
    protected String valueToString(String s) {
        return s;
    }

    @Override
    public String toString() {
        return String.format("StringValue{value=%s,min=%d,max=%d,listable=%b,writable=%b}",
                             value,
                             min,
                             max,
                             listable(),
                             writableByCommand());
    }
}
