import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.arkivanov.mvikotlin:mvikotlin:3.0.2")
    implementation("com.arkivanov.mvikotlin:mvikotlin-extensions-coroutines:3.0.2")
    implementation("com.arkivanov.mvikotlin:mvikotlin-timetravel-proto-internal:3.0.2")
    implementation("com.arkivanov.mvikotlin:mvikotlin-timetravel:3.0.2")
    implementation("com.arkivanov.mvikotlin:mvikotlin-logging:3.0.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}