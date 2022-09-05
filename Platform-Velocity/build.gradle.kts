
repositories {
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.github.LeonMangler:PremiumVanish:6.2.6-4")

    compileOnly("com.velocitypowered:velocity-api:3.1.1")
    kapt("com.velocitypowered:velocity-api:3.1.1")
    annotationProcessor("com.velocitypowered:velocity-api:3.1.1")
    implementation("net.kyori:adventure-text-serializer-legacy:4.11.0")
    compileOnly("net.luckperms:api:5.4")
    implementation("com.google.code.gson:gson:2.9.1")

    implementation(project(":EnhancedGlist-Common"))

    // Caffeine for Cache
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.1")

    // Configurate - Sponge
    implementation("org.spongepowered:configurate-yaml:4.1.2")
    implementation("org.spongepowered:configurate-hocon:4.1.2")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    destinationDirectory.set(file("$rootDir/compiled"))
    archiveClassifier.set("")

    // Relocations for Configurate - Sponge
    relocate("org.spongepowered.configurate", "dev.wirlie.shaded.org.spongepowered.configurate")
    relocate("org.yaml.snakeyaml", "dev.wirlie.shaded.org.yaml.snakeyaml")
    relocate("io.leangen.geantyref", "dev.wirlie.shaded.io.leangen.geantyref")
    relocate("kotlin", "dev.wirlie.shaded.kotlin")
    relocate("com.google.gson", "dev.wirlie.shaded.com.google.gson")
    relocate("com.github.benmanes.caffeine", "dev.wirlie.shaded.com.github.benmanes.caffeine")
}
