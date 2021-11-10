package net.kunmc.lab.value;

import dev.kotx.flylib.command.CommandContext;

public interface SingleValue<T> extends Value<T> {
    boolean validateOnSet(T newValue);

    void onSetValue(T newValue);

    boolean writableByCommand();

    default String failSetMessage(String entryName, T newValue) {
        return "引数の値が不正です.";
    }

    default String succeedSetMessage(String entryName) {
        return entryName + "の値を" + value() + "に設定しました.";
    }

    @Override
    default void sendListMessage(CommandContext ctx, String entryName) {
        ctx.success(entryName + ": " + value());
    }
}
