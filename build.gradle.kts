import java.util.Properties
import java.net.URI

val localProperties = Properties()
localProperties.load(project.rootProject.file("local.properties").inputStream())

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "1.6.21"
    kotlin("kapt") version "1.6.21"
    `maven-publish`
    idea
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.0.1"
}

repositories {
    mavenLocal()
    mavenCentral()
}

allprojects {
    group = "net.wirlie"
    val artifactVersion = System.getenv("ARTIFACT_VERSION") ?: "2.0.0-BETA1"
    val artifactSnapshot = (System.getenv("ARTIFACT_PUBLISH_SNAPSHOT") ?: "true") == "true"
    version = if(artifactSnapshot) "$artifactVersion-SNAPSHOT" else artifactVersion
}

configure(subprojects.filter { !it.name.contains("-API") }) {
    apply(plugin = "com.github.johnrengelman.shadow")
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "kotlin-kapt")
    apply(plugin = "maven-publish")

    repositories {
        mavenLocal()
        mavenCentral()

        maven {
            url = uri("https://nexus.wirlie.net/repository/development/")
            credentials {
                username = localProperties.getProperty("nexus-user")
                password = localProperties.getProperty("nexus-pass")
            }
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    sourceCompatibility = "11"
    targetCompatibility = "11"
}

// Only add Publishing task to -API projects
configure(subprojects.filter { it.name.contains("-API") }) {
    val subProject = this

    publishing {
        publications {
            create<MavenPublication>("maven") {
                artifact("$rootDir/compiled-api/${subProject.name}-${subProject.version}.jar") {
                    extension = "jar"
                }
            }
        }

        repositories {
            val artifactSnapshot = (System.getenv("ARTIFACT_PUBLISH_SNAPSHOT") != null)

            // Nexus
            maven {
                name = "nexus"
                url = if(artifactSnapshot) URI("https://nexus.wirlie.net/repository/public-snapshots/") else URI("https://nexus.wirlie.net/repository/public-releases/")
                credentials {
                    username = localProperties.getProperty("nexus-user")
                    password = localProperties.getProperty("nexus-pass")
                }
            }
        }
    }
}

// Clean, remove compiled folder
tasks.register("cleanCompiledArtifactsFolder") {
    val folder = project.rootProject.file("compiled")
    if(folder.exists()) {
        folder.deleteRecursively()
    }
}
