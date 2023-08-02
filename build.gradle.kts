import org.gradle.api.tasks.testing.logging.TestExceptionFormat

buildscript {
    // fix for https://github.com/mvysny/vaadin-boot-example-gradle/issues/3
    dependencies {
        classpath("com.vaadin:vaadin-prod-bundle:${project.properties["vaadinVersion"]}")
    }
}

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
            if (vaadin.productionMode) {
                exclude(module = "vaadin-dev")
            }
        }
    }

    // Vaadin-Boot
    implementation("com.github.mvysny.vaadin-boot:vaadin-boot:11.3")

    implementation("org.jetbrains:annotations:23.1.0")
    implementation("com.github.mvysny.vaadin-simple-security:vaadin-simple-security:0.2")

    // db
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.flywaydb:flyway-core:9.16.0")
    implementation("com.h2database:h2:2.2.220")
    implementation("com.gitlab.mvysny.jdbiorm:jdbi-orm:1.0")

    // logging
    // currently we are logging through the SLF4J API to SLF4J-Simple. See src/main/resources/simplelogger.properties file for the logger configuration
    implementation("org.slf4j:slf4j-simple:2.0.7")

    // Fast Vaadin unit-testing with Karibu-Testing: https://github.com/mvysny/karibu-testing
    testImplementation("com.github.mvysny.kaributesting:karibu-testing-v24:2.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.3")
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

