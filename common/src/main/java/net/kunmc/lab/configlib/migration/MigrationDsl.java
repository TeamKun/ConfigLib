package net.kunmc.lab.configlib.migration;

import java.util.function.Function;

/**
 * Declarative migration operations for evolving stored config documents.
 * <p>
 * Paths use dot notation for nested objects, for example {@code limits.maxPlayers}.
 * Each operation is recorded in migration reports and applied atomically per load.
 * </p>
 */
public interface MigrationDsl {
    /**
     * Renames a value in place when the source path exists.
     */
    MigrationDsl rename(String from, String to);

    /**
     * Moves a value to another path when the source path exists.
     */
    MigrationDsl move(String from, String to);

    /**
     * Deletes a value when the path exists.
     */
    MigrationDsl delete(String path);

    /**
     * Overwrites the value at the given path.
     */
    MigrationDsl set(String path, Object value);

    /**
     * Writes the value only when the given path is currently missing.
     */
    MigrationDsl defaultValue(String path, Object value);

    /**
     * Converts the current value at the given path when present and compatible with {@code sourceType}.
     */
    <S, T> MigrationDsl convert(String path,
                                Class<S> sourceType,
                                Class<T> targetType,
                                Function<? super S, ? extends T> converter);
}
