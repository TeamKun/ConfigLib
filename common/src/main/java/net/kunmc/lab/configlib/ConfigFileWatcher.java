package net.kunmc.lab.configlib;

import net.kunmc.lab.configlib.exception.LoadingConfigInvalidValueException;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.logging.Level;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

class ConfigFileWatcher implements Closeable {
    private final CommonBaseConfig config;
    private Consumer<Exception> exceptionHandler;
    private WatchService watchService;
    private WatchKey watchKey;

    ConfigFileWatcher(CommonBaseConfig config) {
        this.config = config;
    }

    void start(Timer timer, Consumer<Exception> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        try {
            watchService = FileSystems.getDefault()
                                      .newWatchService();
            watchKey = config.getConfigFolder()
                             .toPath()
                             .register(watchService, ENTRY_MODIFY);
            timer.scheduleAtFixedRate(new WatchTask(), 0, 500);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() {
        try {
            if (watchService != null) {
                watchService.close();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        if (watchKey != null) {
            watchKey.cancel();
        }
    }

    private class WatchTask extends TimerTask {
        @Override
        public void run() {
            for (WatchEvent<?> e : watchKey.pollEvents()) {
                Path filePath = config.getConfigFolder()
                                      .toPath()
                                      .resolve((Path) e.context());
                if (filePath.equals(config.getConfigFile()
                                          .toPath())) {
                    try {
                        config.loadConfig();
                    } catch (LoadingConfigInvalidValueException ex) {
                        config.logger.log(Level.WARNING,
                                          String.format("\"%s\"'s validation failed.",
                                                        ex.getValueField()
                                                          .getName()),
                                          ex);
                    } catch (Exception ex) {
                        exceptionHandler.accept(ex);
                    }
                }
            }
            watchKey.reset();
        }
    }
}
