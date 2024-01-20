import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    java
    application
    id("com.vaadin")
}

defaultTasks("clean", "build")

repositories {
    mavenCentral()
}

dependencies {
    // Vaadin
    implementation("com.vaadin:vaadin-core:${properties["vaadinVersion"]}") {
        afterEvaluate {
            if (vaadin.productionMode.get()) {
                exclude(module = "vaadin-dev")
            }
        }
    }

    // Vaadin-Boot
    implementation("com.github.mvysny.vaadin-boot:vaadin-boot:12.2")

    implementation("org.jetbrains:annotations:24.0.1")
    implementation("com.github.mvysny.vaadin-simple-security:vaadin-simple-security:1.0")

    // db
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.flywaydb:flyway-core:9.22.3")
    implementation("com.h2database:h2:2.2.224")
    implementation("com.gitlab.mvysny.jdbiormvaadin:jdbi-orm-vaadin:1.1")

    // logging
    // currently we are logging through the SLF4J API to SLF4J-Simple. See src/main/resources/simplelogger.properties file for the logger configuration
    implementation("org.slf4j:slf4j-simple:2.0.9")

    // Fast Vaadin unit-testing with Karibu-Testing: https://github.com/mvysny/karibu-testing
    testImplementation("com.github.mvysny.kaributesting:karibu-testing-v24:2.1.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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

