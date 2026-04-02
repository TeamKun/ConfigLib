# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build all modules
./gradlew build

# Build a specific module
./gradlew common:build
./gradlew bukkit:build
./gradlew forge:build

# Run tests
./gradlew test

# Clean
./gradlew clean
```

Gradle daemon is disabled (`gradle.properties`). The project targets Java 8.

## Project Overview

ConfigLib is a type-safe configuration management library for Minecraft servers (Bukkit/Paper and Forge). It provides:

- Reflection-based discovery of config fields (no manual registration)
- GSON-based JSON serialization with custom type adapters
- File watching and auto-reload on change
- Timer-based modification detection (default 500ms interval)
- Auto-generated CLI commands via CommandLib integration
- Validation framework

## Module Structure

- **`common/`** — Platform-agnostic core: `CommonBaseConfig`, all `Value` types, command generation, GSON adapters,
  utilities
- **`bukkit/`** — Bukkit/Paper implementation: `BaseConfig` extending common, Bukkit-specific Value types (
  `LocationValue`, `ItemStackValue`, etc.) and GSON adapters
- **`forge/`** — Forge implementation: `BaseConfig` extending common, Forge-specific Value types and adapters
- **`sample/bukkit/`**, **`sample/forge/`** — Example plugin/mod demonstrating usage
- **`bukkit/test_plugin/`** — Internal test plugin

All source lives under `net.kunmc.lab.configlib`.

## Core Architecture

### Value Wrapper Pattern

All config fields are declared as public final `Value` subclass instances. The base hierarchy:

```
Value<E, T>
  ├── SingleValue<E, T>          (primitives, enums, platform objects)
  │   └── NumericValue<E, T>     (adds arithmetic operators)
  ├── CollectionValue<T, E, U>
  │   ├── ListValue<E, U>
  │   └── SetValue<E, U>
  └── MapValue<K, V, T>
```

`ConfigUtil` + `ReflectionUtil` discover all `Value` fields via reflection at runtime.

### Config Lifecycle

1. `BaseConfig` constructor registers the config and starts async initialization
2. Initial load is async (configurable delay via `Option`)
3. A timer task runs every 500ms checking hash-based modification detection
4. A separate timer polls a `WatchService` for file system changes
5. `saveConfig()` and `loadConfig()` are synchronized

### Adding a New Value Type

1. Extend `SingleValue`, `ListValue`, `SetValue`, or `MapValue` in the appropriate module (`common` for generic types,
   `bukkit`/`forge` for platform-specific)
2. Add a corresponding GSON type adapter in the `gson/` package if serialization isn't handled by GSON defaults
3. If the value should support command modification, implement the relevant `SubCommandType` strategy

### Command Generation

`ConfigCommandBuilder` produces a `ConfigCommand` by inspecting the config's fields. Sub-commands (`get`, `list`,
`modify`, `reload`) are generated per `SubCommandType` enum strategies. Modify sub-commands (`add`, `remove`, `clear`,
`inc`, `dec`, etc.) are generated based on the value type.

### Platform Abstraction

`CommonBaseConfig` contains all core logic. Platform `BaseConfig` classes handle:

- Plugin/mod lifecycle integration (enable/disable hooks)
- Config file directory resolution
- Registering platform-specific GSON adapters

## Usage Pattern

```java
// Define config
public class MyConfig extends BaseConfig {
    public final IntegerValue cooldown = new IntegerValue(10, 0, 100);
    public final StringValue message = new StringValue("Hello");

    public MyConfig(Plugin plugin) { super(plugin); }
}

// Register commands in onEnable()
ConfigCommand cmd = new ConfigCommandBuilder(config).name("myconfig").build();
CommandLib.register(this, cmd);
```

Values support fluent configuration:

```java
new IntegerValue(50)
    .description("Spawn radius")
    .onModify(v -> reload())
    .addValidator(v -> { if (v < 0) throw new InvalidValueException("Must be positive"); })
```
