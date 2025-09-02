plugins {
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.spring") version "2.0.20"
    kotlin("plugin.jpa") version "2.0.20"
    jacoco
}

group = "de.pokescan"
version = "0.1.0"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories { mavenCentral() }

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation(kotlin("reflect"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.xerial:sqlite-jdbc:3.46.1.3")
    implementation("org.hibernate.orm:hibernate-community-dialects")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)   // needed for PR coverage summary
        html.required.set(true)  // uploaded as artifact
        csv.required.set(false)
    }
}

kotlin { jvmToolchain(21) }
