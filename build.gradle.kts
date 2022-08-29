import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.util.Properties

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "1.6.21"
    kotlin("kapt") version "1.6.21"
}

repositories {
    mavenLocal()
    mavenCentral()
}

allprojects {
    group = "com.wirlie"
    version = "2.0.0"
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "kotlin-kapt")
    apply(plugin = "com.github.johnrengelman.shadow")

    val localProperties = Properties().also {
        val file = project.rootProject.file("local.properties")
        if (file.exists()) {
            it.load(project.rootProject.file("local.properties").inputStream())
        }
    }

    repositories {
        mavenLocal()
        mavenCentral()

        maven {
            url = uri("https://nexus.wirlie.net/repository/development/")
            credentials {
                username = System.getenv("NEXUS_USER") ?: localProperties.getProperty("nexus-user")
                password = System.getenv("NEXUS_PASS") ?: localProperties.getProperty("nexus-pass")
            }
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
}

// Clean, remove compiled folder
tasks.withType<Delete> {
    val folder = project.rootProject.file("compiled")
    if(folder.exists()) {
        folder.deleteRecursively()
    }
}
