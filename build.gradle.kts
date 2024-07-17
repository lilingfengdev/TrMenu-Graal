import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.yaml:snakeyaml:2.0")
    }
}

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "8.0.0"
}

group = "io.lilingfeng"

val mcApiVersion: String by project
val repoRef: String by project
fun currentDateString(): String = OffsetDateTime.now(ZoneOffset.UTC).toLocalDate().format(DateTimeFormatter.ISO_DATE)

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.graalvm.sdk:graal-sdk:24.0.1")
    implementation("org.graalvm.polyglot:polyglot:24.0.1")
    implementation("org.graalvm.polyglot:js-community:24.0.1")
    implementation("org.graalvm.truffle:truffle-api:24.0.1")
    implementation("org.graalvm.js:js-scriptengine:24.0.1")
    compileOnly(group = "org.spigotmc", name = "spigot-api", version = "$mcApiVersion+")
}

tasks {
    wrapper {
        gradleVersion = "8.9"
        distributionType = Wrapper.DistributionType.ALL
    }

    processResources {
        val placeholders = mapOf(
            "version" to version,
            "apiVersion" to mcApiVersion,
            "kotlinVersion" to project.properties["kotlinVersion"]
        )

        filesMatching("plugin.yml") {
            expand(placeholders)
        }
    }
    shadowJar{
        mergeServiceFiles()
    }
    // offline jar should be ready to go with all dependencies
    build {
        dependsOn(shadowJar)
    }
}
