
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
    compileOnly("net.md-5:bungeecord:1.19-R0.1-SNAPSHOT")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.github.LeonMangler:PremiumVanish:6.2.6-4")

    compileOnly("net.kyori:adventure-api:4.11.0")
    compileOnly("net.kyori:adventure-platform-bungeecord:4.1.2")
    compileOnly("net.kyori:adventure-text-minimessage:4.11.0")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.11.0")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.google.code.gson:gson:2.9.1")

    compileOnly(project(":EnhancedGlist-Common"))

    // Caffeine for Cache
    compileOnly("com.github.ben-manes.caffeine:caffeine:3.1.1")

    // Configurate - Sponge
    compileOnly("org.spongepowered:configurate-yaml:4.1.2")
}

tasks.withType<org.gradle.jvm.tasks.Jar> {
    destinationDirectory.set(file("$rootDir/compiled-api"))
}
