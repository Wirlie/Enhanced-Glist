
repositories {
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    implementation("org.spongepowered:configurate-hocon:4.1.2")
    // Spigot 1.8 uses 2.2.4
    compileOnly("com.google.code.gson:gson:2.9.0")
}
