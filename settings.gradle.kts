pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.minecraftforge.net/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "ConfigLib"
include("bukkit", "common", "common-java17-tests", "forge")

// TODO: Replace this local CommandLib composite build with released artifacts before publishing.
includeBuild("../CommandLib") {
    dependencySubstitution {
        substitute(module("com.github.Maru32768.CommandLib:common")).using(project(":common"))
        substitute(module("com.github.Maru32768.CommandLib:bukkit")).using(project(":bukkit"))
        substitute(module("com.github.Maru32768.CommandLib:forge")).using(project(":forge"))
        substitute(module("com.github.Maru32768.CommandLib:bukkit-test")).using(project(":bukkit-test"))
        substitute(module("net.kunmc.lab:common")).using(project(":common"))
        substitute(module("net.kunmc.lab:bukkit")).using(project(":bukkit"))
        substitute(module("net.kunmc.lab:forge")).using(project(":forge"))
        substitute(module("net.kunmc.lab:bukkit-test")).using(project(":bukkit-test"))
    }
}
