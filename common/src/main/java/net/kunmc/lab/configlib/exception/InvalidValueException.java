package net.kunmc.lab.configlib.exception;

import net.kunmc.lab.configlib.util.ListUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InvalidValueException extends Exception {
    private final List<String> messages = new ArrayList<>();

    public InvalidValueException() {
        this("不正な値です");
    }

    public InvalidValueException(String message, String... messages) {
        this(ListUtil.asList(message, messages));
    }

    public InvalidValueException(List<String> messages) {
        super(String.join("", messages));
        this.messages.addAll(messages);
    }

    public List<String> getMessages() {
        return Collections.unmodifiableList(messages);
    }
}
