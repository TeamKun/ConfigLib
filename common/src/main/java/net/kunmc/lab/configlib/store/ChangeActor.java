package net.kunmc.lab.configlib.store;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Identifies the actor responsible for a change when known.
 */
public final class ChangeActor {
    private final String name;
    private final String uuid;

    public ChangeActor(@Nullable String name, @Nullable String uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    @Nullable
    public String name() {
        return name;
    }

    @Nullable
    public String uuid() {
        return uuid;
    }

    public boolean isKnown() {
        return name != null || uuid != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ChangeActor)) {
            return false;
        }
        ChangeActor other = (ChangeActor) obj;
        return Objects.equals(name, other.name) && Objects.equals(uuid, other.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, uuid);
    }
}
