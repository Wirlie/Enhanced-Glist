
repositories {
    mavenLocal()
    maven {
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
    maven {
        url = uri("https://jitpack.io")
    }
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
    // Protocolize Repo
    maven {
        url = uri("https://mvn.exceptionflug.de/repository/exceptionflug-public/")
    }
}

dependencies {
    implementation(project(":enhancedglist-common"))
    implementation(project(":enhancedglist-bungeecord-api"))
    implementation(project(":enhancedglist-updater"))
    implementation(project(":enhancedglist-messenger"))

    compileOnly("net.md-5:bungeecord:1.19")

    implementation("net.kyori:adventure-api:4.15.0")
    implementation("net.kyori:adventure-platform-bungeecord:4.3.2")
    implementation("net.kyori:adventure-text-minimessage:4.15.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.15.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.rabbitmq:amqp-client:5.20.0")
    implementation("io.lettuce:lettuce-core:6.3.0.RELEASE")

    // Caffeine for Cache
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")

    // Configurate - Sponge
    implementation("org.spongepowered:configurate-yaml:4.1.2")

    // Third Party Plugin Libraries
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.github.LeonMangler:PremiumVanishAPI:2.9.0-4")

    // Protocolize
    compileOnly("dev.simplix:protocolize-api:2.3.3")
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
    relocate("com.rabbitmq", "dev.wirlie.shaded.com.rabbitmq")
    relocate("io.netty", "dev.wirlie.shaded.io.netty")
    relocate("io.lettuce", "dev.wirlie.shaded.io.lettuce")
    relocate("okhttp3", "dev.wirlie.shaded.okhttp3")
}


tasks.withType<ProcessResources> {

    val props = mutableMapOf(
        Pair("version", version)
    )

    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching(listOf("bungee.yml")) {
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
