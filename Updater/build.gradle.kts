
repositories {
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    implementation("org.spongepowered:configurate-hocon:4.2.0")
    implementation("com.squareup.okhttp3:okhttp:5.5.0")

    // Spigot 1.8 uses 2.2.4
    compileOnly("com.google.code.gson:gson:2.14.0")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
