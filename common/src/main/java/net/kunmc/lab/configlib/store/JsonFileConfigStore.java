package net.kunmc.lab.configlib.store;

import com.google.gson.Gson;

import java.io.File;
import java.util.function.Consumer;

public class JsonFileConfigStore extends FileConfigStore {
    public JsonFileConfigStore(File file, Gson gson) {
        super(file, gson, new JsonConfigFormat(gson));
    }

    public JsonFileConfigStore(File file, Gson gson, Consumer<Exception> exceptionHandler) {
        super(file, gson, new JsonConfigFormat(gson), exceptionHandler);
    }

    public JsonFileConfigStore(File file, Gson gson, Consumer<Exception> exceptionHandler, int maxHistorySize) {
        super(file, gson, new JsonConfigFormat(gson), exceptionHandler, maxHistorySize);
    }
}
