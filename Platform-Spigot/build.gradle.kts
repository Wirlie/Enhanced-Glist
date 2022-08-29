
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

    // Configurate - Sponge
    implementation("org.spongepowered:configurate-yaml:4.1.2")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    destinationDirectory.set(file("$rootDir/compiled"))
    archiveClassifier.set("")

    // Relocations for Configurate - Sponge
    relocate("org.spongepowered.configurate", "dev.wirlie.shaded.org.spongepowered.configurate")
    relocate("org.yaml.snakeyaml", "dev.wirlie.shaded.org.yaml.snakeyaml")
    relocate("io.leangen.geantyref", "dev.wirlie.shaded.io.leangen.geantyref")
    relocate("kotlin", "dev.wirlie.shaded.kotlin")
}
