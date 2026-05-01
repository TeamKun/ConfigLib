plugins {
    java
}

// Override the root project's Java 11 toolchain for this module.
configure<JavaPluginExtension> {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

val commandLibVersion: String by project

repositories {
    mavenCentral()
    maven {
        name = "spigotmc-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven { url = uri("https://libraries.minecraft.net") }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    testImplementation(project(":common"))
    testImplementation("com.google.code.gson:gson:2.10")
    testImplementation("com.github.Maru32768.CommandLib:spigot-testing:${commandLibVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("org.assertj:assertj-core:3.25.1")
    testImplementation("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    testImplementation("com.mojang:brigadier:1.0.18")
}

tasks.test {
    useJUnitPlatform()
}
