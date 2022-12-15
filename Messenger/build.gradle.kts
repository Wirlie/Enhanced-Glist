
repositories {

}

dependencies {
    // Communication types
    implementation("com.rabbitmq:amqp-client:5.16.0")
    implementation("io.lettuce:lettuce-core:6.2.2.RELEASE")
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
