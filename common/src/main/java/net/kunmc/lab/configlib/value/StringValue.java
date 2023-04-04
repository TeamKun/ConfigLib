package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.SuggestionAction;
import net.kunmc.lab.commandlib.argument.StringArgument;
import net.kunmc.lab.commandlib.exception.InvalidArgumentException;
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
    protected transient SuggestionAction<CommandContext> suggestionAction = sb -> {
        allowableStringList.forEach(sb::suggest);
    };

    public StringValue(String value) {
        this(value, 0, Integer.MAX_VALUE);
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

    public StringValue suggestionAction(@NotNull SuggestionAction<CommandContext> action) {
        this.suggestionAction = action;
        return this;
    }

    @Override
    protected void appendArgument(ArgumentBuilder builder) {
        builder.customArgument(new StringArgument(name, option -> {
            option.suggestionAction(suggestionAction)
                  .filter(x -> {
                      if (!allowableStringList.isEmpty()) {
                          if (allowableStringList.stream()
                                                 .noneMatch(s -> s.equals(x))) {
                              throw new InvalidArgumentException(allowableStringList + "の中から文字列を入力してください");
                          }
                      }

                      if (x.length() < min || x.length() > max) {
                          throw new InvalidArgumentException(min + "以上" + max + "以下の文字数で入力してください");
                      }
                  });
        }, type));
    }

    @Override
    protected String argumentToValue(List<Object> argument, CommandContext ctx) {
        return argument.get(0)
                       .toString();
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
