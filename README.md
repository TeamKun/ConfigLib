# ConfigLib

[![](https://jitpack.io/v/TeamKun/ConfigLib.svg)](https://jitpack.io/#TeamKun/ConfigLib)

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

~~ConfigLib depends on [FlyLib Reloaded](https://github.com/TeamKun/flylib-reloaded).~~
~~You must add it in your project.~~  
After 0.9.0, ConfigLib depends on [CommandLib](https://github.com/TeamKun/CommandLib)
