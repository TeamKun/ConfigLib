# ConfigLib

[![](https://jitpack.io/v/TeamKun/ConfigLib.svg)](https://jitpack.io/#TeamKun/ConfigLib)

~~ConfigLib depends on [FlyLib Reloaded](https://github.com/TeamKun/flylib-reloaded).~~  
~~You must add it in your project.~~  
After 0.9.0, ConfigLib depends on [CommandLib](https://github.com/TeamKun/CommandLib)

## Getting Started

### Installation(Gradle Settings)

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
    implementation "com.github.TeamKun.CommandLib:bukkit:0.3.0"
    implementation 'com.github.TeamKun.ConfigLib:bukkit:0.11.2'
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
    implementation "com.github.TeamKun.CommandLib:forge:0.3.1"
    implementation "com.github.TeamKun.ConfigLib:forge:0.11.2"
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

### Sample Code

[Bukkit](/sample/bukkit/src/main/java/net/kunmc/lab/sampleplugin)  
[Forge](/sample/forge/src/main/java/net/kunmc/lab/samplemod)

