package net.kunmc.lab.configlib;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

class ConfigFileIO {
    private ConfigFileIO() {
    }

    static String readJson(File file) {
        try {
            return Files.readLines(file, StandardCharsets.UTF_8)
                        .stream()
                        .collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static void writeJson(File file, String json) {
        try (OutputStreamWriter writer = new OutputStreamWriter(java.nio.file.Files.newOutputStream(file.toPath()),
                                                                StandardCharsets.UTF_8)) {
            writer.write(json);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
