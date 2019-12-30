import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val ktor_version: String by project
val kotlin_version: String by project
val coroutines_version: String by project
val logback_version: String by project
val jackson_version: String by project
val guava_version: String by project

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath("com.github.jengelman.gradle.plugins:shadow:5.2.0")
        classpath("net.sf.proguard:proguard-gradle:6.0.1")
    }
}

plugins {
    application
    kotlin("jvm") version "1.3.61"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "com.github.kr328"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

application {
    mainClassName = "com.github.kr328.webapi.ApplicationKt"
}

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
    maven { url = uri("https://oss.sonatype.org/content/groups/public") }
}

kotlin {
    target {
        version = "1.8"
    }
}

tasks.maybeCreate("shadowJar", ShadowJar::class).apply {
    destinationDir = buildDir.resolve("outputs")
    archiveName = "webapi-backend.jar"
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
    compile("io.ktor:ktor-server-netty:$ktor_version")
    compile("ch.qos.logback:logback-classic:$logback_version")
    compile("io.ktor:ktor-server-core:$ktor_version")
    compile("io.ktor:ktor-client-core:$ktor_version")
    compile("io.ktor:ktor-client-core-jvm:$ktor_version")
    compile("io.ktor:ktor-client-okhttp:$ktor_version")
    compile("com.google.guava:guava:$guava_version")
    compile("com.fasterxml.jackson.core:jackson-core:$jackson_version")
    compile("com.fasterxml.jackson.core:jackson-annotations:$jackson_version")
    compile("com.fasterxml.jackson.core:jackson-databind:$jackson_version")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:$jackson_version")
    compile("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jackson_version")
    testCompile("io.ktor:ktor-server-tests:$ktor_version")
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")
