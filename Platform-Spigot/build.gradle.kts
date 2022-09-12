
repositories {
    // Public Servers
    maven {
        url = uri("https://repo.essentialsx.net/releases/")
    }
}

dependencies {
    compileOnly("net.essentialsx:EssentialsX:2.19.0")
    compileOnly("org.spigotmc:spigot:1.8.8-R0.1-SNAPSHOT")
    compileOnly("com.github.LeonMangler:PremiumVanish:6.2.6-4")
    implementation("net.kyori:adventure-api:4.11.0")
    implementation("net.kyori:adventure-platform-bukkit:4.1.2")
    implementation("net.kyori:adventure-text-minimessage:4.11.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.11.0")

    // Third - Party libraries (only for compilation, allocated at our private repository for development purposes)
    compileOnly("github.jet315:antiafkpro:3.6.3")

    // Configurate - Sponge
    implementation("org.spongepowered:configurate-hocon:4.1.2")
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
}
