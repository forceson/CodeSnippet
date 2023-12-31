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
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}
