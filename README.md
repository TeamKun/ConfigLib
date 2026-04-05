# ConfigLib - A Type-Safe Configuration API for Bukkit and Forge

[![](https://jitpack.io/v/TeamKun/ConfigLib.svg)](https://jitpack.io/#TeamKun/ConfigLib)

ConfigLib is an advanced, type-safe Configuration API designed to simplify configuration management for Bukkit and Forge
developers.

## Features

1. **Type-Safety Configuration Handling**  
   Ensures configuration values are used in a type-safe manner directly within your code, reducing potential runtime
   errors and improving maintainability.
2. **Automatic JSON Mapping**  
   Automatically maps configuration data to Java objects using JSON, eliminating the need for manual parsing and data
   transformation.
3. **Automatic Configuration Reloading**  
   Monitors configuration files and reloads them automatically when changes are detected, ensuring your application
   always works with the latest settings.
4. **Automatic Configuration Saving**  
   Automatically saves updated configuration values to disk, preventing data loss and ensuring persistence.
5. **Command Generation for Configuration Management**  
   Seamlessly integrates with CommandLib to generate commands for managing configurations via the command line.
6. **Schema Migration**  
   Built-in versioned migration system allows safe evolution of configuration structure across releases — handling field
   renames, type changes, and validation constraint changes without breaking existing user data.
7. **Change History & Undo**  
   Every configuration change is automatically recorded with a timestamp. You can browse the history via command and
   revert to any previous state.

## Installation

To ensure stability, we recommend replacing `latest.release` with a specific version such as `0.16.0`.  
You can find the latest version on
the [CommandLib Release Page](https://github.com/TeamKun/CommandLib/releases)
and [ConfigLib Release Page](https://github.com/TeamKun/ConfigLib/releases).

<details>
  <summary>Bukkit</summary>

```groovy
plugins {
    id "com.github.johnrengelman.shadow" version "6.1.0"
}

repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation "com.github.TeamKun.CommandLib:bukkit:latest.release"
    implementation 'com.github.TeamKun.ConfigLib:bukkit:latest.release'
}

shadowJar {
    archiveFileName = "${rootProject.name}-${project.version}.jar"
    relocate "net.kunmc.lab.commandlib", "${project.group}.${project.name.toLowerCase()}.commandlib"
    relocate "net.kunmc.lab.configlib", "${project.group}.${project.name.toLowerCase()}.configlib"
}
tasks.build.dependsOn tasks.shadowJar
  ```

</details>

<details>
  <summary>Forge</summary>

```groovy
plugins {
    id "com.github.johnrengelman.shadow" version "6.1.0"
}

repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation "com.github.TeamKun.CommandLib:forge:latest.release"
    implementation "com.github.TeamKun.ConfigLib:forge:latest.release"
}

shadowJar {
    archiveFileName = "${rootProject.name}-${project.version}.jar"
    dependencies {
        include(dependency("com.github.TeamKun.CommandLib:forge:.*"))
        include(dependency("com.github.TeamKun.ConfigLib:forge:.*"))
    }
    relocate "net.kunmc.lab.commandlib", "${project.group}.${project.name.toLowerCase()}.commandlib"
    relocate "net.kunmc.lab.configlib", "${project.group}.${project.name.toLowerCase()}.configlib"
    finalizedBy("reobfShadowJar")
}

reobf {
    shadowJar {
    }
}
```

</details>

## Code Examples

<details>
<summary>Defining Configuration Classes</summary>

```java
public final class TestConfig extends BaseConfig {
    public final IntegerValue integerValue = new IntegerValue(10);
    public final StringValue stringValue = new StringValue("testValue");

    public TestConfig(Plugin plugin) {
        super(plugin);
        initialize();
    }
}
```

</details>

<details>
<summary>Generating and Registering Configuration Commands</summary>

```java
public final class TestPlugin extends JavaPlugin {
    public void onEnable() {
        TestConfig testConfig = new TestConfig(this);
        Command root = new Command("test") {
        };

        // The following commands will be generated:
        // /test config get <key> - Gets a specific configuration value.
        // /test config list - Gets all configuration values.
        // /test config modify <key> <value> - Sets a specific configuration value.
        // /test config reload - Reloads the configuration file. You may not need it because there's automatic reloading.
        root.addChildren(new ConfigCommandBuilder(testConfig).build());

        CommandLib.register(this, root);
    }
}
```

</details>

<details>
<summary>Registering Multiple Configurations to Commands</summary>

```java
public final class TestPlugin extends JavaPlugin {
    public void onEnable() {
        TestConfigA testConfigA = new TestConfigA(this);
        TestConfigB testConfigB = new TestConfigB(this);
        Command root = new Command("test") {
        };
        root.addChildren(new ConfigCommandBuilder(testConfigA).addConfig(testConfigB)
                                                              .build());

        CommandLib.register(this, root);
    }
}
```

</details>

<details>
<summary>Listening to Configuration Changes</summary>

```java
public final class TestConfig extends BaseConfig {
    public final IntegerValue integerValue = new IntegerValue(10).onModify(x -> {
        System.out.println("Changed integerValue to " + x);
    });

    public TestConfig(Plugin plugin) {
        super(plugin);
        initialize();
    }
}
```

</details>

<details>
<summary>Defining Custom Value Classes</summary>

In this section, we explain how to implement a custom `SingleValue` class and a custom `ListValue` class, based on the
following example class.

```java
// Represents a custom data structure with an integer and a string.
// Used as a value in the configuration.
class TestClass {
    private final int n;
    private final String s;

    public TestClass(int n, String s) {
        this.n = n;
        this.s = s;
    }

    @Override
    public String toString() {
        return "TestClass{" + "n=" + n + ", s='" + s + '\'' + '}';
    }
}
```

#### `SingleValue` Implementation

```java
import java.util.List;

// Custom SingleValue implementation for TestClass.
// Allows storing and manipulating a single instance of TestClass in configurations.
public final class TestClassValue extends SingleValue<TestClass, TestClassValue> {
    public TestClassValue(TestClass initialValue) {
        super(initialValue);
    }

    @Override
    protected List<ArgumentDefinition<TestClass>> argumentDefinitions() {
        List<ArgumentDefinition<TestClass>> res = new ArrayList<>();

        // Defines the arguments required to construct a TestClass instance.
        res.add(new ArgumentDefinition(new IntegerArgument("n"), new StringArgument("s"), (n, s, ctx) -> {
            // Converts command arguments to a TestClass instance.
            return new TestClass(n, s);
        }));

        return res;
    }

    // Converts a TestClass instance to its string representation.
    // This string will be used for command completion suggestions and for get/list command results.
    @Override
    protected String valueToString(TestClass testClass) {
        return testClass.toString();
    }
}
```

#### `ListValue` Implementation

```java
// Custom ListValue implementation for TestClass.
// Allows managing a list of TestClass instances in configurations.
public final class TestClassListValue extends ListValue<TestClass, TestClassListValue> {
    public TestClassListValue(List<TestClass> initialValue) {
        super(initialValue);
    }

    @Override
    protected List<ArgumentDefinition<List<TestClass>>> argumentDefinitionsForAdd() {
        List<ArgumentDefinition<List<TestClass>>> res = new ArrayList<>();

        // Defines the arguments required for the Add command.
        // These arguments will be used to construct a new TestClass instance and add it to the list.
        res.add(new ArgumentDefinition(new IntegerArgument("n"), new StringArgument("s"), (n, s, ctx) -> {
            // Converts command arguments to a new TestClass instance and adds it to the list.
            return Collections.singletonList(new TestClass(n, s));
        }));

        return res;
    }

    @Override
    protected List<ArgumentDefinition<List<TestClass>>> argumentDefinitionsForRemove() {
        List<ArgumentDefinition<List<TestClass>>> res = new ArrayList<>();

        // Defines the arguments required for the Remove command.
        // These arguments will be used to identify which TestClass instance to remove from the list.
        res.add(new ArgumentDefinition(new StringArgument("target", opt -> {
            opt.suggestionAction(sb -> {
                for (TestClass v : value) {
                    sb.suggest(v.toString());
                }
            });
        }, StringArgument.Type.PHRASE_QUOTED), (input, ctx) -> {
            // Finds a TestClass instance to remove based on user input.
            TestClass target = value.stream()
                                    .filter(x -> x.toString()
                                                  .equals(input))
                                    .findFirst()
                                    .orElseThrow(() -> new InvalidArgumentException(input + " is invalid"));
            return Collections.singletonList(target);
        }));

        return res;
    }

    // Converts a TestClass instance to its string representation.
    // This string will be used for command completion suggestions and for get/list command results.
    @Override
    protected String elementToString(TestClass testClass) {
        return testClass.toString();
    }
}
```

These custom Value classes can be used in the same way as built-in Value classes.

```java
public final class TestConfig extends BaseConfig {
    public final TestClassValue testClassValue = new TestClassValue(null);
    public final TestClassListValue testClassListValue = new TestClassListValue(new ArrayList<>());

    public TestConfig(Plugin plugin) {
        super(plugin);
        initialize();
    }
}
```

</details>

<details>
<summary>Schema Migration</summary>

ConfigLib provides a versioned migration system to evolve your configuration schema safely across releases.
Migrations are registered via `Option` and run automatically when an older config file is loaded.
The current version is stored as `_version_` in the JSON file.

```java
public final class MyConfig extends BaseConfig {
    public final StringValue message = new StringValue("hello");
    public final EnumSetValue<EntityType> spawnTypes = new EnumSetValue<>(EntityType.class);

    public MyConfig(Plugin plugin) {
        super(plugin, opt -> opt
                // Version 1: rename field
                .migration(1, ctx -> {
                    ctx.rename("msg", "message");
                })
                // Version 2: type change (IntegerValue -> StringValue)
                .migration(2, ctx -> {
                    if (ctx.has("cooldown")) {
                        ctx.setString("cooldown", String.valueOf(ctx.getInt("cooldown")));
                    }
                })
                // Version 3: remove value that no longer passes validation
                .migration(3, ctx -> {
                    EnumSetValue<EntityType> types = ctx.getObject("spawnTypes",
                                                                   new TypeToken<EnumSetValue<EntityType>>() {
                                                                   }.getType());
                    types.remove(EntityType.GIANT);
                    ctx.setObject("spawnTypes", types);
                }));
        initialize();
    }
}
```

`MigrationContext` provides the following operations:

| Method                                                                  | Description                                          |
|-------------------------------------------------------------------------|------------------------------------------------------|
| `has(key)`                                                              | Check if a key exists                                |
| `getString(key)` / `getInt(key)` / `getDouble(key)` / `getBoolean(key)` | Read primitive values                                |
| `getObject(key, Class<T>)`                                              | Read a complex value by class                        |
| `getObject(key, Type)`                                                  | Read a generic complex value (e.g. with `TypeToken`) |
| `setString(key, value)` / `setInt` / `setDouble` / `setBoolean`         | Write primitive values                               |
| `setObject(key, value)`                                                 | Write a complex value                                |
| `rename(from, to)`                                                      | Rename a field                                       |
| `remove(key)`                                                           | Remove a field                                       |

Migrations only run on existing files that have an older version. New installations start at the latest version and
skip all migrations.

</details>

## Usage Notes

1. Calling `initialize()`
   `initialize()` must be called at the end of the concrete config class constructor, after all fields have been
   assigned. This ensures that the initial config load and file watching start only after the subclass is fully
   constructed.
    ```java
    public final class TestConfig extends BaseConfig {
        public final IntegerValue integerValue = new IntegerValue(10);

        public TestConfig(Plugin plugin) {
            super(plugin);
            initialize(); // Must be the last statement
        }
    }
    ```
2. Asynchronous Change Detection
   Change detection, including when modifying values with the `set` method, is handled asynchronously. Keep this in mind
   to avoid race conditions in your application logic.

## Change History & Undo

Every time a configuration value is modified, the new state is automatically saved to a history file
(`<configName>.history.json`) alongside the config file. The history persists across server restarts and is capped at
50 entries by default (override `createConfigStore()` to change this).

History uses **0-based indexing** where `[0]` is the current (latest) state:

| Command                          | Description                                                              |
|----------------------------------|--------------------------------------------------------------------------|
| `/config history`                | List all history entries with timestamps (hover to preview field values) |
| `/config history <index>`        | Show field values at a specific history index                            |
| `/config undo`                   | Revert to the previous state (equivalent to `undo 1`)                    |
| `/config undo <N>`               | Revert to history index N                                                |
| `/config diff <index>`           | Show what changed between the current state and history index N          |
| `/config diff <index1> <index2>` | Show what changed between two history entries                            |

`/config history N` and `/config undo N` refer to the same index, so you can inspect a snapshot with
`history N` before reverting with `undo N`.

`diff` only shows fields that actually differ, formatted as `fieldName: <old> → <new>`.
It is also accessible as `/config history diff <index>` and `/config history diff <index1> <index2>`.

`undo` is also accessible as `/config history undo` and `/config history undo <N>`.

When there are multiple configs registered under one command, prefix with the config name:
`/config history myConfig`, `/config history myConfig 2`, `/config undo myConfig 2`,
`/config diff myConfig 2`, `/config diff myConfig 1 3`.

### Hiding history commands

To hide the `history` and `undo` subcommands from the generated command tree:

```java
public final class MyConfig extends BaseConfig {
    {
        enableHistory = false;
    }
    // ...
}
```

## Sample Projects

[Bukkit](./sample/bukkit/src/main/java/net/kunmc/lab/sampleplugin)  
[Forge](./sample/forge/src/main/java/net/kunmc/lab/samplemod)
