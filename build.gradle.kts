import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.util.Properties

plugins {
    java
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "com.wirlie"
version = "1.3.1"

val localProperties = Properties().also {
    val file = project.rootProject.file("local.properties")
    if (file.exists()) {
        it.load(project.rootProject.file("local.properties").inputStream())
    }
}

repositories {
    mavenLocal()
    mavenCentral()

    // Wirlie - Private Nexus Server
    maven {
        url = uri("https://nexus.wirlie.net/repository/development/")
        credentials {
            username = System.getenv("NEXUS_USER") ?: localProperties.getProperty("nexus-user")
            password = System.getenv("NEXUS_PASS") ?: localProperties.getProperty("nexus-pass")
        }
    }

    // Public Servers
    maven {
        url = uri("https://repo.essentialsx.net/releases/")
    }
    maven {
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
    maven {
        url = uri("https://jitpack.io")
    }
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencies {
    compileOnly("net.md-5:bungeecord:1.19-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:1.8.8-R0.1-SNAPSHOT")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("net.essentialsx:EssentialsX:2.19.0")
    compileOnly("com.github.LeonMangler:PremiumVanish:6.2.6-4")

    implementation("net.kyori:adventure-api:4.11.0")
    implementation("net.kyori:adventure-platform-bungeecord:4.1.2")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

tasks.withType<ShadowJar> {
    destinationDirectory.set(file("$rootDir/compiled"))
    archiveFileName.set("${baseName}-${version}.${extension}")
}
