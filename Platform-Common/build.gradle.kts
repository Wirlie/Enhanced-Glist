import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

repositories {
    maven {
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
    maven {
        url = uri("https://jitpack.io")
    }
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
    maven {
        url = uri("https://mvn.exceptionflug.de/repository/exceptionflug-public/")
    }
}

dependencies {
    implementation(project(":EnhancedGlist-Updater"))
    implementation(project(":EnhancedGlist-Messenger"))

    implementation("net.kyori:adventure-api:4.11.0")
    implementation("net.kyori:adventure-text-minimessage:4.12.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.11.0")
    implementation("net.kyori:adventure-text-serializer-gson:4.11.0")
    implementation("com.google.code.gson:gson:2.9.1")

    compileOnly("net.luckperms:api:5.4")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("dev.simplix:protocolize-api:2.2.2")

    // Caffeine for Cache
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.1")

    // Configurate - Sponge
    implementation("org.spongepowered:configurate-yaml:4.1.2")
    implementation("org.spongepowered:configurate-hocon:4.1.2")

    testImplementation(kotlin("test"))
    testImplementation("org.mockito:mockito-core:4.8.0")
    testImplementation("org.mockito:mockito-junit-jupiter:4.8.0")
    testImplementation("org.mockito:mockito-inline:4.8.0")

}

tasks.withType<ProcessResources> {

    val props = mutableMapOf(
        Pair("build_version", version),
        Pair("build_job_name", System.getenv("JOB_NAME") ?: "unknown"),
        Pair("build_id", System.getenv("BUILD_ID") ?: "unknown"),
        Pair("target_release", System.getenv("BUILD_TARGET_RELEASE") ?: "unknown"),
        Pair("build_full_hash", System.getenv("GIT_COMMIT") ?: "unknown"),
        Pair("build_branch", System.getenv("GIT_BRANCH") ?: "unknown"),
        Pair("build_timestamp", System.currentTimeMillis()),
    )

    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching(
        listOf(
            "config.conf", "en/gui-glist-menu.conf", "es/gui-glist-menu.conf",
            "en/gui-slist-menu.conf", "es/gui-slist-menu.conf", "messages/es.conf", "messages/en.conf"
        )
    ) {
        expand(props)
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

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Test> {
    testLogging {
        // set options for log level LIFECYCLE
        events = setOf(
            TestLogEvent.FAILED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED,
            TestLogEvent.STANDARD_OUT
        )

        exceptionFormat = TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true

        // set options for log level DEBUG and INFO
        debug {
            events = setOf(
                TestLogEvent.STARTED,
                TestLogEvent.FAILED,
                TestLogEvent.PASSED,
                TestLogEvent.SKIPPED,
                TestLogEvent.STANDARD_ERROR,
                TestLogEvent.STANDARD_OUT
            )
            exceptionFormat = TestExceptionFormat.FULL
        }
        info.events = debug.events
        info.exceptionFormat = debug.exceptionFormat

        fun repeatLength(char: Char, times: Int): String {
            val builder = StringBuilder()
            for(i in 0 until times) {
                builder.append(char)
            }
            return builder.toString()
        }

        afterSuite(KotlinClosure2({ desc: TestDescriptor, result: TestResult ->
            if (desc.parent == null) { // will match the outermost suite
                val output = "Results: ${result.resultType} (${result.testCount} tests, ${result.successfulTestCount} passed, ${result.failedTestCount} failed, ${result.skippedTestCount} skipped)"
                val startItem = "|  "
                val endItem = "  |"
                val repeatLength = startItem.length + output.length + endItem.length
                println("\n" + (repeatLength('-', repeatLength)) + "\n" + startItem + output + endItem + "\n" + (repeatLength('-', repeatLength)))
            }
        }))
    }
}
