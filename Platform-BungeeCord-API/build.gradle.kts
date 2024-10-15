
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
}

dependencies {
    compileOnly("net.md-5:bungeecord:1.19")
    compileOnly("net.luckperms:api:5.4")

    compileOnly("net.kyori:adventure-api:4.17.0")
    compileOnly("net.kyori:adventure-platform-bungeecord:4.3.4")
    compileOnly("net.kyori:adventure-text-minimessage:4.17.0")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.17.0")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.google.code.gson:gson:2.11.0")

    compileOnly(project(":enhancedglist-common"))

    // Caffeine for Cache
    compileOnly("com.github.ben-manes.caffeine:caffeine:3.1.8")

    // Configurate - Sponge
    compileOnly("org.spongepowered:configurate-yaml:4.1.2")
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
