# ConfigLib

## Getting Started

### Using ConfigLib

<details>
  <summary>Gradle</summary>

  ```groovy
  plugins {
    id "com.github.johnrengelman.shadow" version "6.1.0"
}
  ```

  ```groovy
  allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
  ```

  ```groovy
  dependencies {
    implementation 'com.github.TeamKun:ConfigLib:[version]'
}

  ```

</details>

<details>
  <summary>Maven</summary>

  ```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
  ```

  ```xml

<dependency>
    <groupId>com.github.TeamKun</groupId>
    <artifactId>ConfigLib</artifactId>
    <version>[version]</version>
</dependency>
  ```

</details>

ConfigLib depends on [FlyLib Reloaded](https://github.com/TeamKun/flylib-reloaded).  
You must add it in your project.

First, implement a Config Class which extends BaseConfig Class.

```Java
import net.kunmc.lab.configlib.BaseConfig;
import net.kunmc.lab.configlib.value.*;
import org.bukkit.plugin.Plugin;

public class Config extends BaseConfig {
    // You must use wrapper class
    public IntegerValue intValue = new IntegerValue(10);
    // If you want Value not to be serialized, mark it as transient
    public transient IntegerValue intValue2 = new IntegerValue(10);
    public MaterialValue materialValue = new MaterialValue(Material.DIRT);

    public Config(Plugin plugin) {
        super(plugin);
    }
}
```

Then, build a ConfigCommand in JavaPlugin#onEnable and register it on your command.

```Java
import net.kunmc.lab.configlib.ConfigCommand;
import net.kunmc.lab.configlib.ConfigCommandBuilder;

public class SamplePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        Config config = new Config(this);
        // If you want save config as file, should do it
        config.saveConfigIfAbsent();
        // If you want load config from file, should do it
        config.loadConfig();

        // build a ConfigCommand
        ConfigCommand configCommand = new ConfigCommandBuilder(config).build();

        Flylib.create(this, builder -> {
            builder.command(new TestCommand(configCommand));
        });
    }
}

public class TestCommand extends Command {
    public TestCommand(ConfigCommand configCommand) {
        super("test");
        // register ConfigCommand
        children(configCommand);
    }
}
```

With above, you will be able to use commands like below.

```
// show list of current config values
/test config list

// show current value of intValue's value
/test config get intValue

// update intValue's value to 5
/test config modify intValue set 5

// reload config file
/test config reload
```