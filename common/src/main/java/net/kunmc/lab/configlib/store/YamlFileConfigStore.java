package net.kunmc.lab.configlib.store;

import com.google.gson.Gson;

import java.io.File;
import java.util.function.Consumer;

public class YamlFileConfigStore extends FileConfigStore {
    public YamlFileConfigStore(File file, Gson gson) {
        this(file, gson, (e) -> {
        }, 50, UnknownKeyPolicy.PRESERVE);
    }

    public YamlFileConfigStore(File file,
                               Gson gson,
                               Consumer<Exception> exceptionHandler,
                               int maxHistorySize,
                               UnknownKeyPolicy unknownKeyPolicy) {
        super(file, gson, new YamlConfigFormat(gson), exceptionHandler, maxHistorySize, unknownKeyPolicy);
    }
}
