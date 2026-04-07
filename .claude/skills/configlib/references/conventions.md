# ConfigLib Conventions and Recommended Patterns

## Config class definition

Declare all config fields as `public final` Value instances. Call `initialize()` at the end of the constructor.

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

`initialize()` triggers the first load and starts the file-watch timer. Calling it before all fields are assigned will cause fields to be missed.

## Registering commands in onEnable

```java
@Override
public void onEnable() {
    config = new MyConfig(this);

    ConfigCommand cmd = new ConfigCommandBuilder(config)
            .name("myconfig")
            .build();
    CommandLib.register(this, cmd);
}
```

`ConfigCommandBuilder` auto-generates `list`, `reload`, `reset`, `history`, `undo`, `diff`, and per-field get/modify subcommands. Use `disableXxxCommand()` to suppress subcommands you don't want.

## Value type selection guide

**Single values**

| Type | Class |
|---|---|
| `int` | `IntegerValue` |
| `double` | `DoubleValue` |
| `float` | `FloatValue` |
| `boolean` | `BooleanValue` |
| `String` | `StringValue` |
| `Enum` | `EnumValue<E>` |
| `Nameable` impl | `NameableObjectValue<E>` |
| `Location` (Bukkit) | `LocationValue` |
| `ItemStack` (Bukkit) | `ItemStackValue` |
| `Material` (Bukkit) | `MaterialValue` |
| `Team` (Bukkit) | `TeamValue` |
| `UUID` (Bukkit) | `UUIDValue` |
| `BlockData` (Bukkit) | `BlockDataValue` |
| `Vector` (Bukkit) | `VectorValue` |
| `Particle` (Bukkit) | `ParticleValue` |

**Collections**

| Type | Class |
|---|---|
| `List<String>` | `StringListValue` |
| `List<ItemStack>` | `ItemStackListValue` |
| `Set<String>` | `StringSetValue` |
| `Set<Enum>` | `EnumSetValue<E>` |
| `Set<Material>` | `MaterialSetValue` |
| `Set<Team>` | `TeamSetValue` |
| `Set<UUID>` | `UUIDSetValue` |
| `Set<BlockData>` | `BlockDataSetValue` |
| `Set<Location>` | `LocationSetValue` |
| `Set<Nameable>` | `NameableObjectSetValue<E>` |

**Maps** — naming convention is `Key2ValueMapValue`:
`String2IntegerMapValue`, `Enum2DoubleMapValue<E>`, `Team2BooleanMapValue`, `UUID2StringMapValue`, etc.

**Pairs**
`Integer2IntegerPairValue`, `Double2DoublePairValue`, `Integer2ObjectPairValue<V>`, etc.

## Fluent Value configuration

All methods return `this` for chaining:

```java
public final IntegerValue spawnRadius = new IntegerValue(50, 1, 500)
        .description("Radius in blocks around spawn where PvP is disabled")
        .onModify(v -> reloadSpawnZone())
        .addValidator(v -> {
            if (v % 2 != 0) throw new InvalidValueException("Must be even");
        })
        .formatter(v -> v + " blocks")
        .executableIf(ctx -> ctx.getSender().hasPermission("myplugin.admin"));
```

**Key methods on `Value<E, T>`:**

| Method | Purpose |
|---|---|
| `description(String)` | Shown in list/get commands |
| `onInitialize(Consumer<E>)` | Fires once after first load |
| `onModify(Consumer<E>)` | Fires on any change: file reload, command modify, or programmatic `value(E)` setter (timer-detected, up to ~100ms delay) |
| `onModify(listener, true)` | Also fires on initialize |
| `addValidator(Validator<E>)` | Validates on command modify and file load; throw `InvalidValueException` to reject |
| `formatter(Function<E, String>)` | Custom display string for list/get |
| `entryName(String)` | Override the field name used in commands |
| `executableIf(ExecutionCondition)` | Guard command execution; throw `CommandPrerequisiteException` to block |

**`SingleValue` extras:**

| Method | Purpose |
|---|---|
| `writableByCommand(false)` | Make read-only via command (still writable by file) |
| `onModifyCommand(Consumer<E>)` | Fires only on command modify, not file reload |
| `successMessage(Function)` | Custom success message after command modify |

**`CollectionValue` extras:**

| Method | Purpose |
|---|---|
| `addableByCommand(false)` | Disable add subcommand |
| `removableByCommand(false)` | Disable remove subcommand |
| `clearableByCommand(false)` | Disable clear subcommand |
| `onAdd(Consumer<T>)` | Fires after add command |
| `onRemove(Consumer<T>)` | Fires after remove command |
| `onClear(Runnable)` | Fires after clear command |

**`MapValue` extras** — same pattern with `puttableByCommand` / `onPut` / `onRemove` / `onClear`.

## Reading values

```java
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
```

## Listening for any config change

```java
config.onChange(() -> rebuildCache());
```

`onChange` fires after every successful reload (from file or command), after undo, after command modify, and after any programmatic `value(E)` call is detected by the modification timer (up to ~100ms delay). When `onChange` fires due to a programmatic change, the new value is also auto-saved to the config file.

## Migrations

Use when renaming a field or changing a value's format across versions:

```java
public MyConfig(Plugin plugin) {
    super(plugin, opt -> opt
        .migration(1, ctx -> {
            // rename "radius" → "spawnRadius"
            if (ctx.has("radius")) {
                ctx.set("spawnRadius", ctx.getInt("radius"));
                ctx.remove("radius");
            }
        })
    );
    initialize();
}
```

Migrations run in version order on load when `_version_` in the JSON is lower than the latest migration key.

## Multiple configs in one command tree

```java
ConfigCommand cmd = new ConfigCommandBuilder(mainConfig)
        .addConfig(worldConfig)
        .addConfig(combatConfig)
        .name("myplugin")
        .sort() // sorts configs alphabetically
        .build();
```

Fields with the same name across configs are exposed as `configEntryName.fieldName` to avoid collision.

## Custom Value types

Extend `SingleValue`, `ListValue`, `SetValue`, or `MapValue` in your own project:

```java
public class WorldValue extends SingleValue<World, WorldValue> {
    public WorldValue(World defaultWorld) {
        super(defaultWorld);
    }

    @Override
    protected List<ArgumentDefinition<World>> argumentDefinitions() {
        return ListUtil.of(new ArgumentDefinition<>(
            new StringArgument("world", opt -> opt.suggestionAction(sb ->
                Bukkit.getWorlds().forEach(w -> sb.suggest(w.getName()))
            )),
            (name, ctx) -> Bukkit.getWorld(name)
        ));
    }

    @Override
    protected String valueToString(World world) {
        return world.getName();
    }
}
```

If custom GSON serialization is needed, register a type adapter via `BaseConfig.Option#gsonCustomizer`.