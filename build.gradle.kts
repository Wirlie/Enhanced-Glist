import java.util.Properties
import java.net.URI

// Variables for username and password for publishing releases/snapshots to Maven repository.
var publishUsername = ""
var publishPassword = ""

// local.properties file is an optional file, only required for publishing releases/snapshots to Maven repository.
if(project.rootProject.file("local.properties").exists()) {
    val localProperties = Properties()
    localProperties.load(project.rootProject.file("local.properties").inputStream())

    if (localProperties.contains("nexus-user-publish") && localProperties.contains("nexus-pass-publish")) {
        publishUsername = localProperties.getProperty("nexus-user-publish")
        publishPassword = localProperties.getProperty("nexus-pass-publish")
    }
}

// Plugins
plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "1.8.10"
    kotlin("kapt") version "1.8.10"
    `maven-publish`
    idea
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.7"
}

// Repositories
repositories {
    mavenLocal()
    mavenCentral()
}

// Common configurations for all projects
allprojects {
    // The group of the artifact
    group = "net.wirlie"
    // The current artifact version
    val baseVersion = "2.0.0-BETA4"
    // Version can be modified by the Continuous Integration server via an environment variable (for example Jenkins)
    val artifactVersion = System.getenv("ARTIFACT_VERSION") ?: baseVersion
    // Environment variable used by the Continuos Integration server to define if the current build is a snapshot or a release
    // If environment variable is absent then we will treat the build as a snapshot build
    val artifactSnapshot = (System.getenv("ARTIFACT_PUBLISH_SNAPSHOT") ?: "true") == "true"
    // Finally, we set the gradle version according to the previous modifications, if the build is a snapshot build we will
    // add the -SNAPSHOT suffix to the version.
    version = if(artifactSnapshot) "$artifactVersion-SNAPSHOT" else artifactVersion
}

// Apply shadow plugin to all subprojects except for the API project.
configure(subprojects.filter { !it.name.contains("-API") }) {
    apply(plugin = "com.github.johnrengelman.shadow")
}

// For all subprojects...
subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "kotlin-kapt")
    apply(plugin = "maven-publish")

    repositories {
        mavenLocal()
        mavenCentral()
    }
}

// Use Java 11 as the target version
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    sourceCompatibility = "11"
    targetCompatibility = "11"
}

// Also configure kotlin to use Java 11 as target version
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
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
                    username = publishUsername
                    password = publishPassword
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
