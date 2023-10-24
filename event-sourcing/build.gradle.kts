plugins {
    kotlin("jvm") version "1.8.20"
    id("org.ec4j.editorconfig") version "0.0.3"
    checkstyle
    application
}

allprojects {
    repositories {
        mavenCentral()
    }

    group = "com.forceson"
    version = "1.0-SNAPSHOT"
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.assertj:assertj-core:3.23.1")
    testImplementation("io.github.autoparams:autoparams:1.1.1")
    testImplementation("io.github.autoparams:autoparams-mockito:1.1.1")
    testImplementation("io.mockk:mockk:1.13.4")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}
