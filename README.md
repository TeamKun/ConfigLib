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
    }
}
```

</details>

## Usage Notes

1. Asynchronous Config Loading  
   Initial configuration loading is performed asynchronously. As a result, JSON values might not be immediately reflected
   after creating an instance. If immediate reflection is required, call the `loadConfig` method within the constructor,
   as shown in the example below:
    ```java
    public final class TestConfig extends BaseConfig {
        public final IntegerValue integerValue = new IntegerValue(10);

        public TestConfig(Plugin plugin) {
            super(plugin);
            loadConfig();
        }
    }
    ```
2. Asynchronous Change Detection  
   Change detection, including when modifying values with the `set` method, is handled asynchronously. Keep this in mind
   to avoid race conditions in your application logic.

## Sample Projects

[Bukkit](./sample/bukkit/src/main/java/net/kunmc/lab/sampleplugin)  
[Forge](./sample/forge/src/main/java/net/kunmc/lab/samplemod)
