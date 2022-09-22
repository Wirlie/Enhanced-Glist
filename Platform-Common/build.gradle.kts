
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
    maven {
        url = uri("https://mvn.exceptionflug.de/repository/exceptionflug-public/")
    }
}

dependencies {
    implementation(project(":EnhancedGlist-Updater"))
    implementation(project(":EnhancedGlist-Messenger"))

    implementation("net.kyori:adventure-api:4.11.0")
    implementation("net.kyori:adventure-text-minimessage:4.11.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.11.0")
    implementation("net.kyori:adventure-text-serializer-gson:4.11.0")
    implementation("com.google.code.gson:gson:2.9.1")

    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.github.LeonMangler:PremiumVanish:6.2.6-4")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("dev.simplix:protocolize-api:2.2.2")

    // Caffeine for Cache
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.1")

    // Configurate - Sponge
    implementation("org.spongepowered:configurate-yaml:4.1.2")
    implementation("org.spongepowered:configurate-hocon:4.1.2")
}

tasks.withType<ProcessResources> {

    val props = mutableMapOf(
        Pair("build_version", version),
        Pair("build_job_name", System.getenv("JOB_NAME") ?: "unknown"),
        Pair("build_id", System.getenv("BUILD_ID") ?: "unknown"),
        Pair("target_release", System.getenv("BUILD_TARGET_RELEASE") ?: "unknown"),
        Pair("build_full_hash", System.getenv("GIT_COMMIT") ?: "unknown"),
        Pair("build_branch", System.getenv("GIT_BRANCH") ?: "unknown"),
        Pair("build_timestamp", System.currentTimeMillis()),
    )

    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching(
        listOf(
            "metadata.conf", "config.conf", "en/gui-glist-menu.conf", "es/gui-glist-menu.conf",
            "en/gui-slist-menu.conf", "es/gui-slist-menu.conf", "messages/es.conf", "messages/en.conf"
        )
    ) {
        expand(props)
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
