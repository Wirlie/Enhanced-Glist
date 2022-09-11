import java.util.Properties
import java.net.URI

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "1.6.21"
    kotlin("kapt") version "1.6.21"
    `maven-publish`
}

repositories {
    mavenLocal()
    mavenCentral()
}

allprojects {
    group = "com.wirlie"
    val artifactVersion = System.getenv("ARTIFACT_VERSION") ?: "1.2"
    val artifactSnapshot = (System.getenv("ARTIFACT_PUBLISH_SNAPSHOT") != null)
    version = if(artifactSnapshot) "$artifactVersion-SNAPSHOT" else artifactVersion
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "kotlin-kapt")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "maven-publish")

    val localProperties = Properties()
    localProperties.load(project.rootProject.file("local.properties").inputStream())

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

    publishing {
        publications {
            create<MavenPublication>("maven") {
                artifact("$rootDir/compiled/${this.name}-${this.version}.jar") {
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
tasks.withType<Delete> {
    val folder = project.rootProject.file("compiled")
    if(folder.exists()) {
        folder.deleteRecursively()
    }
}
