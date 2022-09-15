
repositories {
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
    implementation(project(":EnhancedGlist-Common"))
    implementation(project(":EnhancedGlist-BungeeCord-API"))

    compileOnly("net.md-5:bungeecord:1.19-R0.1-SNAPSHOT")

    implementation("net.kyori:adventure-api:4.11.0")
    implementation("net.kyori:adventure-platform-bungeecord:4.1.2")
    implementation("net.kyori:adventure-text-minimessage:4.11.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.11.0")
    implementation("com.google.code.gson:gson:2.9.1")

    // Caffeine for Cache
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.1")

    // Configurate - Sponge
    implementation("org.spongepowered:configurate-yaml:4.1.2")

    // Third Party Plugin Libraries
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.github.LeonMangler:PremiumVanishAPI:2.7.11-2")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    destinationDirectory.set(file("$rootDir/compiled"))

    if(archiveClassifier.get() == "all") {
        archiveClassifier.set("")
    }

    // Relocations for Configurate - Sponge
    relocate("org.spongepowered.configurate", "dev.wirlie.shaded.org.spongepowered.configurate")
    relocate("org.yaml.snakeyaml", "dev.wirlie.shaded.org.yaml.snakeyaml")
    relocate("io.leangen.geantyref", "dev.wirlie.shaded.io.leangen.geantyref")
    relocate("kotlin", "dev.wirlie.shaded.kotlin")
    relocate("com.google.gson", "dev.wirlie.shaded.com.google.gson")
    relocate("com.github.benmanes.caffeine", "dev.wirlie.shaded.com.github.benmanes.caffeine")
    relocate("net.kyori", "dev.wirlie.shaded.net.kyori")
}

tasks.withType<ProcessResources> {

    val props = mutableMapOf(
        Pair("build-version", version),
        Pair("build-job-name", System.getenv("JOB_NAME")),
        Pair("build-id", System.getenv("BUILD_ID")),
        Pair("build-full-hash", System.getenv("GIT_COMMIT")),
        Pair("build-branch", System.getenv("GIT_BRANCH")),
        Pair("build-timestamp", System.currentTimeMillis()),
    )

    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("metadata.conf") {
        expand(props)
    }
}
