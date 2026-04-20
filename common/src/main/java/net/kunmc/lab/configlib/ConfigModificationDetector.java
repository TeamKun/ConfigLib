package net.kunmc.lab.configlib;

import net.kunmc.lab.configlib.exception.ConfigValidationException;
import net.kunmc.lab.configlib.schema.ConfigSchemaEntry;

import java.util.*;

class ConfigModificationDetector {
    private final CommonBaseConfig config;
    private final Map<ConfigSchemaEntry<?>, Integer> entryToHashMap = new HashMap<>();
    private ValidationFailureSignature lastValidationFailure;

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

    void detect() {
        if (!config.initialized) {
            return;
        }

        config.withIoLock(() -> {
            List<ConfigSchemaEntry<?>> modifiedEntries = new ArrayList<>();
            for (Map.Entry<ConfigSchemaEntry<?>, Integer> entry : entryToHashMap.entrySet()) {
                ConfigSchemaEntry<?> schemaEntry = entry.getKey();
                int newHash = schemaEntry.sourceHash();
                if (newHash == entry.getValue()) {
                    continue;
                }

                modifiedEntries.add(schemaEntry);
            }

            if (modifiedEntries.isEmpty()) {
                return;
            }

            // Validate the whole live config before any side effects. Direct field updates are
            // allowed in memory, but invalid values must not be persisted or recorded in history.
            try {
                config.validateCurrentConfig();
            } catch (ConfigValidationException e) {
                logValidationFailureIfChanged(e);
                return;
            }
            lastValidationFailure = null;

            // Notify listeners only after validation succeeds. Invalid direct updates remain in
            // memory, but they are not treated as accepted config changes.
            modifiedEntries.forEach(schemaEntry -> {
                schemaEntry.dispatchModify();
                entryToHashMap.put(schemaEntry, schemaEntry.sourceHash());
            });

            // Saving can still validate the serialized round trip, so keep history and onChange
            // behind a successful write.
            try {
                config.saveConfigIfPresent();
            } catch (ConfigValidationException e) {
                logValidationFailureIfChanged(e);
                return;
            }
            lastValidationFailure = null;
            config.pushHistory();
            // Saving may merge disk edits back into the live config. Reset hashes to the
            // persisted state instead of the pre-save values detected in this timer tick.
            initializeHash();
            config.dispatchOnChange();
        });
    }

    private void logValidationFailureIfChanged(ConfigValidationException e) {
        ValidationFailureSignature signature = new ValidationFailureSignature(e);
        if (signature.equals(lastValidationFailure)) {
            // Direct field updates can leave an invalid value in memory across timer ticks.
            // Log the first failure for a given invalid state, then stay quiet until it changes.
            return;
        }

        lastValidationFailure = signature;
        config.logValidationFailure(e);
    }

    private class DetectionTask extends TimerTask {
        @Override
        public void run() {
            detect();
        }
    }

    private static final class ValidationFailureSignature {
        private final String path;
        private final int valueHash;

        private ValidationFailureSignature(ConfigValidationException e) {
            this.path = e.path()
                         .asString();
            this.valueHash = Objects.hashCode(e.value());
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ValidationFailureSignature)) {
                return false;
            }

            ValidationFailureSignature other = (ValidationFailureSignature) obj;
            return valueHash == other.valueHash && path.equals(other.path);
        }

        @Override
        public int hashCode() {
            return Objects.hash(path, valueHash);
        }
    }
}
