package net.kunmc.lab.configlib.store;

/**
 * Thrown by {@link UnknownKeyPolicy#FAIL} when it encounters a key outside the config schema.
 */
public class UnknownConfigKeyException extends RuntimeException {
    public UnknownConfigKeyException(String path) {
        super("Unknown config key: " + path);
    }
}
