package net.kunmc.lab.configlib.store;

import com.google.gson.Gson;

import java.io.File;
import java.util.function.Consumer;

public class YamlFileConfigStore extends FileConfigStore {
    public YamlFileConfigStore(File file, Gson gson) {
        super(file, gson, new YamlConfigFormat(gson));
    }

    public YamlFileConfigStore(File file, Gson gson, Consumer<Exception> exceptionHandler) {
        super(file, gson, new YamlConfigFormat(gson), exceptionHandler);
    }

    public YamlFileConfigStore(File file, Gson gson, Consumer<Exception> exceptionHandler, int maxHistorySize) {
        super(file, gson, new YamlConfigFormat(gson), exceptionHandler, maxHistorySize);
    }
}
