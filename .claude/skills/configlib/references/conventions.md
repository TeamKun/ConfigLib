# ConfigLib Conventions and Recommended Patterns

## Config class definition

Use either the Value API or the POJO API. Call `initialize()` at the end of the constructor.

Prefer Value fields when the config needs custom command parsing, tab-completion, listeners, or collection/map
modification commands. Prefer POJO fields for simple annotation-driven scalar settings.

```java
public class MyConfig extends BaseConfig {
    public final IntegerValue cooldown = new IntegerValue(30, 0, 300);
    public final StringValue welcomeMessage = new StringValue("Hello!");
    public final BooleanValue enabled = new BooleanValue(true);

    public MyConfig(Plugin plugin) {
        super(plugin);
        initialize(); // must be last
    }
}
```

`initialize()` triggers the first load and starts the file-watch timer. Calling it before all fields are assigned will
cause fields to be missed.

## Registering commands in onEnable

```java
public final class MyPlugin extends JavaPlugin {
    private MyConfig config;

    @Override
    public void onEnable() {
        config = new MyConfig(this);

        ConfigCommand cmd = new ConfigCommandBuilder(config).name("myconfig")
                                                            .build();
        CommandLib.register(this, cmd);
    }
}
```

`ConfigCommandBuilder` auto-generates `list`, `reload`, `reset`, `history`, `undo`, `diff`, and per-field get/modify
subcommands. Value fields get their normal `set`, `reset`, numeric, collection, and map operations. Mutable POJO scalar
fields (`String`, `boolean`, `int`, `float`, `double`, `enum`, and boxed equivalents) get set commands only. Complex
POJO fields remain get-only.

Use `.disableList()`, `.disableReload()`, `.disableReset()`, `.disableHistory()`, `.disableGet()`, `.disableModify()` to
suppress subcommands you don't want.

## Value type selection guide

**Single values**

| Type                 | Class                    |
|----------------------|--------------------------|
| `int`                | `IntegerValue`           |
| `double`             | `DoubleValue`            |
| `float`              | `FloatValue`             |
| `boolean`            | `BooleanValue`           |
| `String`             | `StringValue`            |
| `Enum`               | `EnumValue<E>`           |
| `Nameable` impl      | `NameableObjectValue<E>` |
| `Location` (Bukkit)  | `LocationValue`          |
| `ItemStack` (Bukkit) | `ItemStackValue`         |
| `Material` (Bukkit)  | `MaterialValue`          |
| `Team` (Bukkit)      | `TeamValue`              |
| `UUID` (Bukkit)      | `UUIDValue`              |
| `BlockData` (Bukkit) | `BlockDataValue`         |
| `Vector` (Bukkit)    | `VectorValue`            |
| `Particle` (Bukkit)  | `ParticleValue`          |

**Collections**

| Type              | Class                       |
|-------------------|-----------------------------|
| `List<String>`    | `StringListValue`           |
| `List<ItemStack>` | `ItemStackListValue`        |
| `Set<String>`     | `StringSetValue`            |
| `Set<Enum>`       | `EnumSetValue<E>`           |
| `Set<Material>`   | `MaterialSetValue`          |
| `Set<Team>`       | `TeamSetValue`              |
| `Set<UUID>`       | `UUIDSetValue`              |
| `Set<BlockData>`  | `BlockDataSetValue`         |
| `Set<Location>`   | `LocationSetValue`          |
| `Set<Nameable>`   | `NameableObjectSetValue<E>` |

**Maps** — naming convention is `Key2ValueMapValue`:
`String2IntegerMapValue`, `Enum2DoubleMapValue<E>`, `Team2BooleanMapValue`, `UUID2StringMapValue`, etc.

**Pairs**
`Integer2IntegerPairValue`, `Double2DoublePairValue`, `Integer2ObjectPairValue<V>`, etc.

## Fluent Value configuration

All methods return `this` for chaining:

```java
public final class MyConfig extends BaseConfig {
    public final IntegerValue spawnRadius = new IntegerValue(50, 1, 500).description(
                                                                                "Radius in blocks around spawn where PvP is disabled")
                                                                        .onModify(v -> reloadSpawnZone())
                                                                        .addValidator(v -> {
                                                                            if (v % 2 != 0) {
                                                                                throw new InvalidValueException(
                                                                                        "Must be even");
                                                                            }
                                                                        })
                                                                        .formatter(v -> v + " blocks")
                                                                        .executableIf(ctx -> ctx.getSender()
                                                                                                .hasPermission(
                                                                                                        "myplugin.admin"));

    public MyConfig(Plugin plugin) {
        super(plugin);
        initialize();
    }
}
```

**Key methods on `Value<E, T>`:**

| Method                             | Purpose                                                                                                                  |
|------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| `description(String)`              | Shown in list/get commands                                                                                               |
| `onInitialize(Consumer<E>)`        | Fires once after first load                                                                                              |
| `onModify(Consumer<E>)`            | Fires on any change: file reload, command modify, or programmatic `value(E)` setter (timer-detected, up to ~100ms delay) |
| `onModify(listener, true)`         | Also fires on initialize                                                                                                 |
| `addValidator(Validator<E>)`       | Validates on command modify and file load; throw `InvalidValueException` to reject                                       |
| `formatter(Function<E, String>)`   | Custom display string for list/get                                                                                       |
| `entryName(String)`                | Override the field name used in commands                                                                                 |
| `executableIf(ExecutionCondition)` | Guard command execution; throw `CommandPrerequisiteException` to block                                                   |

**`SingleValue` extras:**

| Method                         | Purpose                                             |
|--------------------------------|-----------------------------------------------------|
| `disableModify()`              | Make read-only via command (still writable by file) |
| `onModifyCommand(Consumer<E>)` | Fires only on command modify, not file reload       |
| `successMessage(Function)`     | Custom success message after command modify         |

**`CollectionValue` extras:**

| Method                  | Purpose                    |
|-------------------------|----------------------------|
| `disableAdd()`          | Disable add subcommand     |
| `disableRemove()`       | Disable remove subcommand  |
| `disableClear()`        | Disable clear subcommand   |
| `onAdd(Consumer<T>)`    | Fires after add command    |
| `onRemove(Consumer<T>)` | Fires after remove command |
| `onClear(Runnable)`     | Fires after clear command  |

**`MapValue` extras** — same pattern with `disablePut()` / `disableRemove()` / `disableClear()` / `onPut` / `onRemove` /
`onClear`.

## Reading values

```java
class Example {
    void test() {
        // .value() returns the raw wrapped type
        int r = config.spawnRadius.value();

        // SingleValue has Optional-style helpers
        config.welcomeMessage.ifPresent(msg -> player.sendMessage(msg));
        String msg = config.welcomeMessage.orElse("Welcome!");

        // BooleanValue shortcuts
        config.enabled.isTrue();
        config.enabled.ifTrue(() -> doSomething());

        // NumericValue arithmetic (returns the result, does not mutate)
        int doubled = config.cooldown.multiply(2);
    }
}
```

## Consistent programmatic access

Use `CommonBaseConfig#mutate` when changing values from custom code. It treats all changes in the block as one
ConfigLib change: save, one history entry, modification hash refresh, and `onChange` dispatch.

```java
public final class Example {
    private final MyConfig config;

    public Example(MyConfig config) {
        this.config = config;
    }

    void updateConfig() {
        config.mutate(() -> {
            config.cooldown.value(45);
            config.welcomeMessage.value("Welcome back!");
        });
    }
}
```

Use `CommonBaseConfig#inspect` for read-only calculations that need a stable view across multiple fields or collection
contents. `inspect` does not save, append history, or notify listeners.

```java
public final class Example {
    private final MyConfig config;

    public Example(MyConfig config) {
        this.config = config;
    }

    String summary() {
        return config.inspect(() -> "cooldown=" + config.cooldown.value() + ", enabled=" + config.enabled.value());
    }
}
```

Prefer `mutate` over calling `value(...)` directly when the change is initiated by plugin/mod code. Direct `value(...)`
assignment is still detected by the timer, but it is not as explicit and may be delayed until the next detection tick.

## Listening for any config change

```java
class Example {
    void test() {
        config.onChange(() -> rebuildCache());
    }
}
```

`onChange` fires after every successful reload (from file or command), after undo, after command modify, and after any
programmatic `value(E)` call is detected by the modification timer (up to ~100ms delay). When `onChange` fires due to a
programmatic change, the new value is also auto-saved to the config file.

## Migrations

Use when renaming a field or changing a value's format across versions:

```java
public final class MyConfig extends BaseConfig {
    public final IntegerValue spawnRadius = new IntegerValue(30);

    public MyConfig(Plugin plugin) {
        super(plugin, opt -> opt.migration(1, ctx -> {
            // rename "radius" -> "spawnRadius"
            if (ctx.has("radius")) {
                ctx.set("spawnRadius", ctx.getInt("radius"));
                ctx.remove("radius");
            }
        }));
        initialize();
    }
}
```

Migrations run in version order on load when `_version_` in the config file is lower than the latest migration key.
`_version_` is a ConfigLib-reserved key exposed as `ConfigKeys.VERSION`; do not use it as an application config field.

**Adding new fields:**

When a config class gains a new field, existing config files usually do not contain that key yet. ConfigLib keeps the
field's Java-side default value for missing keys during load, so adding a field does not require a migration by itself.

This only applies to missing keys. If a config file explicitly contains `null`, the loaded `null` is validated normally:
POJO fields require `@ConfigNullable`, and `Value` fields must accept `null` in their validators.

Use a migration when an existing key must be renamed, moved, removed, converted to another type, or changed to a
different value based on old file contents.

## Config files and history files

Bukkit and Forge `BaseConfig` use YAML files by default. The standard Gson builder is configured with pretty printing,
and YAML output uses block style with two-space indentation so files are suitable for direct editing.
`Value.description(...)` and POJO `@Description` annotations are written as YAML comments when the config is saved.

Config files can be edited externally. On save, ConfigLib compares the last loaded snapshot, current memory state, and
current disk state. If the same top-level field changed both in memory and on disk, the disk value wins and the
discarded
memory value is logged.

History files use the same extension as the config file:

- `example.yml` -> `example.history.yml`
- `example.json` -> `example.history.json`

YAML history files are written under a `history:` key:

```yaml
history:
  - _ts_: 1710000000000
    cooldown:
      value: 45
```

Older top-level-array YAML history files are still readable.

## Multiple configs in one command tree

```java
public final class CommandRegistration {
    ConfigCommand buildCommand(CommonBaseConfig mainConfig,
                               CommonBaseConfig worldConfig,
                               CommonBaseConfig combatConfig) {
        return new ConfigCommandBuilder(mainConfig).addConfig(worldConfig)
                                                   .addConfig(combatConfig)
                                                   .name("myplugin")
                                                   .sort() // sorts configs alphabetically
                                                   .build();
    }
}
```

Fields with the same name across configs are exposed as `configEntryName.fieldName` to avoid collision.

## POJO API

Use plain Java fields instead of `Value` objects for a simpler, annotation-driven style.
Non-`static`, non-`transient` fields are automatically treated as config entries.
Value API and POJO fields can be mixed freely in the same config class.

### Flat POJO config

```java
public final class ServerConfig extends BaseConfig {
    @Description("Maximum number of players.")
    @Range(min = 1, max = 100)
    public int maxPlayers = 20;

    @Description("Message shown on join.")
    public String motd = "Welcome!";

    @ConfignNullable
    public String adminContact = null;

    public transient RuntimeCache cache = new RuntimeCache(); // excluded

    public ServerConfig(Plugin plugin) {
        super(plugin);
        initialize();
    }
}
```

### Annotations

| Annotation        | Target        | Effect                                            |
|-------------------|---------------|---------------------------------------------------|
| `@Description`    | field         | YAML comment + hover text in commands             |
| `@Range`          | numeric field | Validates `min ≤ value ≤ max` on load and command |
| `@ConfigNullable` | field         | Allows `null`; fields without it reject `null`    |

### POJO generated commands

All POJO fields appear in `list`, `get`, `diff`, and `history` output. Mutable scalar POJO fields can also be changed
with set commands:

```text
/config maxPlayers 30
/config maxPlayers set 30
/config arena.maxArenas 10
```

Supported POJO modify types are `String`, `boolean`, `int`, `float`, `double`, `enum`, and boxed equivalents. `@Range`
is applied to numeric command input and file load validation.

Collection, map, object-valued leaf fields, Minecraft-specific object fields, and top-level `final` POJO fields are
read-only in generated per-field commands. Nested POJO, immutable class, and record leaf fields are still modifiable
when their leaf type is supported.

POJO fields do not get per-field `reset`, `inc`, `dec`, `add`, `remove`, `clear`, or `put` commands. Use the Value API
when those operations or custom command behavior are needed.

### Nested POJO (mutable inner class)

Declare a `static` inner class. Entries appear with dotted paths (`arena.maxArenas`).

```java
public final class PluginConfig extends BaseConfig {
    public ArenaSettings arena = new ArenaSettings();

    public PluginConfig(Plugin plugin) {
        super(plugin);
        initialize();
    }

    public static final class ArenaSettings {
        @Description("Maximum number of arenas.")
        @Range(min = 1, max = 50)
        public int maxArenas = 5;
        public String defaultName = "arena";
    }
}
```

### Nested immutable class (constructor/accessor model)

All non-`static` non-`transient` fields must be `private final`. An all-args constructor whose parameter types match
the field declaration order is used to write values. Accessor methods (same name as field, no args) are optional for
reading.

```java
public static final class ArenaSettings {
    @Description("Maximum number of arenas.")
    @Range(min = 1, max = 50)
    private final int maxArenas;
    private final String defaultName;

    public ArenaSettings(int maxArenas, String defaultName) {
        this.maxArenas = maxArenas;
        this.defaultName = defaultName;
    }

    public int maxArenas() {
        return maxArenas;
    }

    public String defaultName() {
        return defaultName;
    }
}
```

### Nested record (Java 16+ runtime)

```java
public record ArenaSettings(@Description("Maximum number of arenas.") @Range(min = 1, max = 50) int maxArenas,
                            String defaultName) {
}
```

Nesting is supported to any depth. Records can be nested inside records.

### When to use POJO API vs Value API

| Need                                             | Use       |
|--------------------------------------------------|-----------|
| Simple primitive / String fields                 | POJO API  |
| Existing Java value classes (immutable, records) | POJO API  |
| Minecraft-specific types (World, Location, …)    | Value API |
| Custom tab-completion or argument parsing        | Value API |
| Simple scalar set commands                       | POJO API  |
| Reset, arithmetic, collection, or map commands   | Value API |
| Dynamic validators with complex logic            | Value API |

## Custom Value types

Extend `SingleValue`, `ListValue`, `SetValue`, or `MapValue` in your own project:

```java
public class WorldValue extends SingleValue<World, WorldValue> {
    public WorldValue(World defaultWorld) {
        super(defaultWorld);
    }

    @Override
    protected List<ArgumentDefinition<World>> argumentDefinitions() {
        return List.of(new ArgumentDefinition<>(new StringArgument("world",
                                                                   opt -> opt.suggestionAction(sb -> Bukkit.getWorlds()
                                                                                                           .forEach(w -> sb.suggest(
                                                                                                                   w.getName())))),
                                                (name, ctx) -> Bukkit.getWorld(name)));
    }

    @Override
    protected String valueToString(World world) {
        return world.getName();
    }
}
```

If custom GSON serialization is needed, register a type adapter via `BaseConfig.Option#gsonCustomizer`.
