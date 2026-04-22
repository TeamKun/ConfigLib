package net.kunmc.lab.configlib.store;

/**
 * Describes why a history snapshot was recorded.
 */
public enum HistorySource {
    /**
     * Initial snapshot for a newly created config with no existing history.
     */
    INITIAL,

    /**
     * Initial snapshot created after applying migrations to an existing stored config.
     */
    MIGRATION,

    /**
     * Normal runtime updates such as command changes, undo results, or explicit mutations.
     */
    PROGRAMMATIC
}
