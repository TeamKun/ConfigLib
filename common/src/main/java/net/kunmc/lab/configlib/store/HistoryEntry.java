package net.kunmc.lab.configlib.store;

import net.kunmc.lab.configlib.CommonBaseConfig;

public class HistoryEntry {
    private final long timestamp;
    private final CommonBaseConfig config;

    public HistoryEntry(long timestamp, CommonBaseConfig config) {
        this.timestamp = timestamp;
        this.config = config;
    }

    public long timestamp() {
        return timestamp;
    }

    public CommonBaseConfig config() {
        return config;
    }
}
