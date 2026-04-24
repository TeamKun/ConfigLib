package net.kunmc.lab.configlib.store;

import net.kunmc.lab.configlib.CommonBaseConfig;

public class HistoryEntry {
    private final long timestamp;
    private final ChangeTrace trace;
    private final CommonBaseConfig config;

    /**
     * @param timestamp creation time in epoch milliseconds
     * @param trace     why this snapshot was recorded
     * @param config    config state captured in this history entry
     */
    public HistoryEntry(long timestamp, ChangeTrace trace, CommonBaseConfig config) {
        this.timestamp = timestamp;
        this.trace = trace;
        this.config = config;
    }

    public long timestamp() {
        return timestamp;
    }

    public ChangeSource source() {
        return trace.source();
    }

    public ChangeTrace trace() {
        return trace;
    }

    /**
     * Returns the deserialized config snapshot for this history entry.
     */
    public CommonBaseConfig config() {
        return config;
    }
}
