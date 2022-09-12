
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
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.11.0")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.google.code.gson:gson:2.9.1")

    compileOnly(project(":EnhancedGlist-Common"))

    // Caffeine for Cache
    compileOnly("com.github.ben-manes.caffeine:caffeine:3.1.1")

    // Configurate - Sponge
    compileOnly("org.spongepowered:configurate-yaml:4.1.2")
    compileOnly("org.spongepowered:configurate-hocon:4.1.2")
}

tasks.withType<org.gradle.jvm.tasks.Jar> {
    destinationDirectory.set(file("$rootDir/compiled-api"))
}
