package net.kunmc.lab.configlib.store;

import net.kunmc.lab.configlib.CommonBaseConfig;

public class HistoryEntry {
    private final long timestamp;
    private final HistorySource source;
    private final CommonBaseConfig config;

    /**
     * @param timestamp creation time in epoch milliseconds
     * @param source    why this snapshot was recorded
     * @param config    config state captured in this history entry
     */
    public HistoryEntry(long timestamp, HistorySource source, CommonBaseConfig config) {
        this.timestamp = timestamp;
        this.source = source;
        this.config = config;
    }

    public long timestamp() {
        return timestamp;
    }

    public HistorySource source() {
        return source;
    }

    /**
     * Returns the deserialized config snapshot for this history entry.
     */
    public CommonBaseConfig config() {
        return config;
    }
}
