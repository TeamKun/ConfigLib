package net.kunmc.lab.configlib.store;

/**
 * Describes where an accepted config change originated.
 */
public enum ChangeSource {
    /**
     * Initial snapshot for a newly created config with no existing history.
     */
    INITIAL,

    /**
     * Snapshot created after applying migrations to stored config data.
     */
    MIGRATION,

    /**
     * Change accepted through a ConfigLib command execution.
     */
    COMMAND,

    /**
     * Change accepted through direct programmatic mutation.
     */
    PROGRAMMATIC,

    /**
     * Change loaded from the backing store, such as an external file edit.
     */
    FILE,

    /**
     * Change produced by reverting to an earlier history entry.
     */
    UNDO
}
