apply(plugin = "idea")
apply(plugin = "org.jetbrains.gradle.plugin.idea-ext")

repositories {
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    // Protocolize Repo
    maven {
        url = uri("https://mvn.exceptionflug.de/repository/exceptionflug-public/")
    }
    // PremiumVanish
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    compileOnly("net.luckperms:api:5.4")

    compileOnly("com.velocitypowered:velocity-api:3.1.1")
    kapt("com.velocitypowered:velocity-api:3.1.1")
    annotationProcessor("com.velocitypowered:velocity-api:3.1.1")
    implementation("net.kyori:adventure-text-serializer-legacy:4.15.0")
    compileOnly("net.luckperms:api:5.4")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.rabbitmq:amqp-client:5.20.0")
    implementation("io.lettuce:lettuce-core:6.3.0.RELEASE")

    implementation(project(":enhancedglist-common"))
    implementation(project(":enhancedglist-velocity-api"))
    implementation(project(":enhancedglist-updater"))
    implementation(project(":enhancedglist-messenger"))

    // Caffeine for Cache
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")

    // Configurate - Sponge
    implementation("org.spongepowered:configurate-yaml:4.1.2")
    implementation("org.spongepowered:configurate-hocon:4.1.2")

    // Protocolize
    compileOnly("dev.simplix:protocolize-api:2.3.3")

    // PremiumVanish
    compileOnly("com.github.LeonMangler:PremiumVanishAPI:2.9.0-4")
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
    relocate("com.google.gson", "dev.wirlie.shaded.com.google.gson")
    relocate("com.github.benmanes.caffeine", "dev.wirlie.shaded.com.github.benmanes.caffeine")
    relocate("io.netty", "dev.wirlie.shaded.io.netty")
    relocate("io.lettuce", "dev.wirlie.shaded.io.lettuce")
    relocate("okhttp3", "dev.wirlie.shaded.okhttp3")
}

val templateSource = file("src/main/templates")
val templateDest = layout.buildDirectory.dir("generated/sources/templates")
val generateTemplates = tasks.register<Copy>("generateTemplates") {
    val props = mutableMapOf(Pair("version", project.version))

    inputs.properties(props)
    from(templateSource)
    into(templateDest)
    expand(props)
}

sourceSets.main.get().java.srcDir(generateTemplates.map { it.outputs })

fun org.gradle.plugins.ide.idea.model.IdeaProject.settings(block: org.jetbrains.gradle.ext.ProjectSettings.() -> Unit) =
    (this@settings as ExtensionAware).extensions.configure(block)

fun org.jetbrains.gradle.ext.ProjectSettings.taskTriggers(block: org.jetbrains.gradle.ext.TaskTriggersConfig.() -> Unit) =
    (this@taskTriggers as ExtensionAware).extensions.configure("taskTriggers", block)

fun Project.idea(block: org.gradle.plugins.ide.idea.model.IdeaModel.() -> Unit) =
    (this as ExtensionAware).extensions.configure("idea", block)

rootProject.idea {
    project {
        settings {
            taskTriggers {
                afterSync(generateTemplates)
            }
        }
    }
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
