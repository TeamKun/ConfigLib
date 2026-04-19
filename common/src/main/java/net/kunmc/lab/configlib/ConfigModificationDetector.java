package net.kunmc.lab.configlib;

import net.kunmc.lab.configlib.schema.ConfigSchemaEntry;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

class ConfigModificationDetector {
    private final CommonBaseConfig config;
    private final Map<ConfigSchemaEntry<?>, Integer> entryToHashMap = new HashMap<>();

    ConfigModificationDetector(CommonBaseConfig config) {
        this.config = config;
    }

    void initializeHash() {
        config.schema()
              .entries()
              .forEach(entry -> entryToHashMap.put(entry, entry.sourceHash()));
    }

    boolean isModified() {
        for (Map.Entry<ConfigSchemaEntry<?>, Integer> entry : entryToHashMap.entrySet()) {
            if (entry.getKey()
                     .sourceHash() != entry.getValue()) {
                return true;
            }
        }
        return false;
    }

    void start(Timer timer, int period) {
        timer.scheduleAtFixedRate(new DetectionTask(), period, period);
    }

    private class DetectionTask extends TimerTask {
        @Override
        public void run() {
            if (!config.initialized) {
                return;
            }

            config.withIoLock(() -> {
                boolean modified = false;
                for (Map.Entry<ConfigSchemaEntry<?>, Integer> entry : entryToHashMap.entrySet()) {
                    ConfigSchemaEntry<?> schemaEntry = entry.getKey();
                    int newHash = schemaEntry.sourceHash();
                    if (newHash == entry.getValue()) {
                        continue;
                    }

                    schemaEntry.dispatchModify();
                    entryToHashMap.put(schemaEntry, newHash);
                    modified = true;
                }

                if (modified) {
                    config.saveConfigIfPresent();
                    config.pushHistory();
                    // Saving may merge disk edits back into the live config. Reset hashes to the
                    // persisted state instead of the pre-save values detected in this timer tick.
                    initializeHash();
                    config.dispatchOnChange();
                }
            });
        }
    }
}
