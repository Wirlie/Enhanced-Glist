
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
    implementation("net.kyori:adventure-api:4.11.0")
    implementation("net.kyori:adventure-text-minimessage:4.11.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.11.0")
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
