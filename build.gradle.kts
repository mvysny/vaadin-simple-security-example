import com.vaadin.gradle.getBooleanProperty
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    java
    application
    alias(libs.plugins.vaadin)
}

defaultTasks("clean", "build")

repositories {
    mavenCentral()
}

dependencies {
    // Vaadin
    implementation(libs.vaadin.core) {
        // https://github.com/vaadin/flow/issues/18572
        if (vaadin.productionMode.map { v -> getBooleanProperty("vaadin.productionMode") ?: v }.get()) {
            exclude(module = "vaadin-dev")
        }
    }

    // Vaadin-Boot
    implementation(libs.vaadinboot)

    implementation(libs.jetbrains.annotations)
    implementation("com.github.mvysny.vaadin-simple-security:vaadin-simple-security:1.0")

    // db
    implementation(libs.hikaricp)
    implementation(libs.flyway)
    implementation(libs.h2)
    implementation("com.gitlab.mvysny.jdbiormvaadin:jdbi-orm-vaadin:1.1")

    // logging
    // currently we are logging through the SLF4J API to SLF4J-Simple. See src/main/resources/simplelogger.properties file for the logger configuration
    implementation(libs.slf4j.simple)

    // Fast Vaadin unit-testing with Karibu-Testing: https://github.com/mvysny/karibu-testing
    testImplementation(libs.kaributesting)
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
    }
}

application {
    mainClass.set("com.example.security.Main")
}
