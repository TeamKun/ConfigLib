import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.github.jengelman.gradle.plugins:shadow:6.1.0")
    }
}

apply(plugin = "java")
apply(plugin = "com.github.johnrengelman.shadow")

group = "net.kunmc.lab"
version = "1.0.0"

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven { url = uri("https://jitpack.io") }
    flatDir { dirs("server/cache", "libs") }
}

dependencies {
    "compileOnly"(mapOf("name" to "patched_1.16.5"))
    "implementation"("com.google.code.gson:gson:2.10")
    "implementation"("org.apache.commons:commons-lang3:3.12.0")
    "implementation"("com.github.TeamKun.CommandLib:bukkit:0.17.1")
}

configure<JavaPluginExtension> {
    toolchain.languageVersion.set(JavaLanguageVersion.of(11))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"

    // patched_1.16.5 内の古い GSON より implementation の GSON を優先する
    val implJars = configurations.getByName("compileClasspath")
        .filter { it.name.startsWith("gson") }
    classpath = files(implJars) + classpath
}

tasks.named<Jar>("jar") {
    doFirst {
        copy {
            from(".")
            into(layout.buildDirectory.dir("resources/main"))
            include("LICENSE*")
        }
    }
}

configure<SourceSetContainer> {
    named("main") {
        java.srcDir("../src/main/java")
        java.srcDir("../../common/src/main/java")
    }
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveFileName.set("${rootProject.name}-${project.version}.jar")
    relocate("net.kunmc.lab.commandlib", "${project.group}.${project.name.toLowerCase()}.commandlib")
    relocate("net.kunmc.lab.configlib", "${project.group}.${project.name.toLowerCase()}.configlib")
    relocate("com.google.gson", "${project.group}.${project.name.toLowerCase()}.gson")
}
tasks.named("build") { dependsOn(tasks.named("shadowJar")) }

tasks.named<ProcessResources>("processResources") {
    val props = mapOf("name" to rootProject.name, "version" to version, "MainClass" to getMainClassFQDN(projectDir.toPath()))
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.register<Copy>("copyToServer") {
    group = "copy"
    mustRunAfter(tasks.named("build"))
    from(layout.buildDirectory.file("libs/${rootProject.name}-${version}.jar"))
    into("./server/plugins")
}

tasks.register("buildAndCopy") {
    group = "build"
    dependsOn(tasks.named("build"), tasks.named("copyToServer"))
}

tasks.register("downloadServerJar") {
    val url = uri("https://api.papermc.io/v2/projects/paper/versions/1.16.5/builds/794/downloads/paper-1.16.5-794.jar").toURL()
    val file = projectDir.toPath().toAbsolutePath().resolve("server/server.jar").toFile()
    if (!file.exists()) {
        url.openStream().use { stream ->
            Files.copy(stream, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
    }
}

tasks.register("generatePatchedJar") {
    group = "setup"
    dependsOn(tasks.named("downloadServerJar"))
    val serverDir = projectDir.toPath().toAbsolutePath().resolve("server").toString()
    val file = Path.of(serverDir, "cache/patched_1.16.5.jar").toFile()
    if (!file.exists()) {
        try {
            val p = Runtime.getRuntime().exec("java -jar $serverDir/server.jar nogui", emptyArray(), Path.of(serverDir).toFile())
            p.waitFor()
            p.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

tasks.register("copyDefaultServerProperties") {
    group = "setup"
    dependsOn(tasks.named("downloadServerJar"))
    val serverDir = projectDir.toPath().toAbsolutePath().resolve("server")
    val dst = serverDir.resolve("server.properties")
    if (!dst.toFile().exists()) {
        Files.copy(serverDir.resolve("server.default.properties"), dst)
    }
}

fun getMainClassFQDN(projectPath: Path): String {
    return Files.walk(projectPath)
        .filter { it.fileName.toString().endsWith(".java") }
        .filter { path -> Files.lines(path).anyMatch { it.contains("extends JavaPlugin") } }
        .findFirst()
        .get()
        .toString()
        .replace("\\", ".").replace("/", ".")
        .replace(Regex(".*src\\.main\\.java\\.|.java$"), "")
}
