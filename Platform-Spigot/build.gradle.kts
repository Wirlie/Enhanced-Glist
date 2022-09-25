
repositories {
    // Public Servers
    maven {
        url = uri("https://repo.essentialsx.net/releases/")
    }
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot:1.8.8-R0.1-SNAPSHOT")

    implementation("net.kyori:adventure-api:4.11.0")
    implementation("net.kyori:adventure-platform-bukkit:4.1.2")
    implementation("net.kyori:adventure-text-minimessage:4.11.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.11.0")
    implementation("com.rabbitmq:amqp-client:5.16.0")
    implementation("io.lettuce:lettuce-core:6.2.0.RELEASE")

    // Third Party libraries with public API
    compileOnly("net.essentialsx:EssentialsX:2.19.7")
    compileOnly("com.github.LeonMangler:PremiumVanish:6.2.6-4")
    compileOnly("com.github.mbax:VanishNoPacket:3.22")
    compileOnly("com.github.xtomyserrax:StaffFacilities:5.0.8")

    // Third Party libraries without public API (only for compilation, allocated at our private repository for development purposes)
    compileOnly("github.jet315:antiafkpro:3.6.3")

    // Configurate - Sponge
    implementation("org.spongepowered:configurate-yaml:4.1.2")
    implementation("org.spongepowered:configurate-hocon:4.1.2")

    implementation(project(":EnhancedGlist-Updater"))
    implementation(project(":EnhancedGlist-Messenger"))
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
    relocate("com.github.benmanes.caffeine", "dev.wirlie.shaded.com.github.benmanes.caffeine")
    relocate("net.kyori", "dev.wirlie.shaded.net.kyori")
    relocate("com.rabbitmq", "dev.wirlie.shaded.com.rabbitmq")
    relocate("io.netty", "dev.wirlie.shaded.io.netty")
    relocate("io.lettuce", "dev.wirlie.shaded.io.lettuce")
    relocate("okhttp3", "dev.wirlie.shaded.okhttp3")
}



tasks.withType<ProcessResources> {

    val props = mutableMapOf(
        Pair("build_version", version),
        Pair("version", version),
        Pair("build_job_name", System.getenv("JOB_NAME") ?: "unknown"),
        Pair("build_id", System.getenv("BUILD_ID") ?: "unknown"),
        Pair("target_release", System.getenv("BUILD_TARGET_RELEASE") ?: "unknown"),
        Pair("build_full_hash", System.getenv("GIT_COMMIT") ?: "unknown"),
        Pair("build_branch", System.getenv("GIT_BRANCH") ?: "unknown"),
        Pair("build_timestamp", System.currentTimeMillis()),
    )

    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching(listOf("metadata.conf", "config.conf", "plugin.yml")) {
        expand(props)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
