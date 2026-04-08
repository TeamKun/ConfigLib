buildscript {
    repositories {
        gradlePluginPortal()
        maven { url = uri("https://files.minecraftforge.net/maven") }
    }
    dependencies {
        classpath("net.minecraftforge.gradle:ForgeGradle:5.1.+")
    }
}

apply(plugin = "net.minecraftforge.gradle")

val commandLibVersion: String by project

configure<net.minecraftforge.gradle.common.util.MinecraftExtension> {
    mappings("snapshot", "20210309-1.16.5")
}

repositories {
    maven {
        url = uri("https://libraries.minecraft.net")
    }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    "minecraft"("net.minecraftforge:forge:1.16.5-36.2.31")
    compileOnly("org.jetbrains:annotations:20.1.0")
    api(project(":common"))
    compileOnly("com.github.TeamKun.CommandLib:forge:$commandLibVersion")
}
