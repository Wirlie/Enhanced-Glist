
repositories {
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("net.luckperms:api:5.4")

    compileOnly("com.velocitypowered:velocity-api:3.1.1")
    kapt("com.velocitypowered:velocity-api:3.1.1")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.15.0")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.google.code.gson:gson:2.10.1")

    compileOnly(project(":enhancedglist-common"))

    // Caffeine for Cache
    compileOnly("com.github.ben-manes.caffeine:caffeine:3.1.8")

    // Configurate - Sponge
    compileOnly("org.spongepowered:configurate-yaml:4.1.2")
    compileOnly("org.spongepowered:configurate-hocon:4.1.2")
}

tasks.withType<org.gradle.jvm.tasks.Jar> {
    destinationDirectory.set(file("$rootDir/compiled-api"))
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
