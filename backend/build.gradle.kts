
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(ktorLibs.plugins.ktor)
    id("org.jetbrains.kotlin.plugin.serialization") version libs.versions.kotlin.get()
}

group = "com.example"
version = "1.0.0-SNAPSHOT"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

kotlin {
    jvmToolchain(21)
}
dependencies {
    implementation(ktorLibs.client.cio)
    implementation(ktorLibs.client.core)
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(ktorLibs.server.config.yaml)
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.cors)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.server.routingOpenapi)
    implementation(ktorLibs.server.swagger)
    implementation(libs.logback.classic)

    implementation("org.apache.commons:commons-csv:1.12.0")

    testImplementation(kotlin("test"))
    testImplementation(ktorLibs.server.testHost)
}
