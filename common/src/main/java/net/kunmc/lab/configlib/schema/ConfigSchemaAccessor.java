package net.kunmc.lab.configlib.schema;

public interface ConfigSchemaAccessor<E> {
    E get();

    void set(E value);
}
