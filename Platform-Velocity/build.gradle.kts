apply(plugin = "idea")
apply(plugin = "org.jetbrains.gradle.plugin.idea-ext")

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
    implementation(project(":EnhancedGlist-Velocity-API"))
    implementation(project(":EnhancedGlist-Updater"))

    // Caffeine for Cache
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.1")

    // Configurate - Sponge
    implementation("org.spongepowered:configurate-yaml:4.1.2")
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
    relocate("com.google.gson", "dev.wirlie.shaded.com.google.gson")
    relocate("com.github.benmanes.caffeine", "dev.wirlie.shaded.com.github.benmanes.caffeine")
}

val templateSource = file("src/main/templates")
val templateDest = layout.buildDirectory.dir("generated/sources/templates")
val generateTemplates = tasks.register<Copy>("generateTemplates") {
    val props = mutableMapOf(Pair("version", project.version))

    println(templateDest.get().asFile.absolutePath)

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
