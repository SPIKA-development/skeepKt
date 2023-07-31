plugins {
    application
}

apply(plugin = "kotlin")
apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

dependencies {
    testImplementation("org.testng:testng:7.1.0")
    add("implementation", project(":shared"))
    add("implementation", libs.ktor.server.netty)
    add("implementation", libs.ktor.server.core)
    add("implementation", libs.ktor.server.websockets)
    add("implementation", libs.logback)

    implementation(libs.kotlinx.uuid.exposed)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.sessions)
    implementation(libs.jetbrains.exposed.core)
    implementation(libs.jetbrains.exposed.dao)
    implementation(libs.jetbrains.exposed.jdbc)
    implementation(libs.jdbc.h2)
    implementation(libs.ktor.server.content.negotation)
    implementation(libs.ktor.client.content.negotation)

    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.ktor.server.content.negotation)
    testImplementation(libs.ktor.client.content.negotation)
    testImplementation(libs.ktor.client.websockets)
    testImplementation(libs.ktor.serialization.kotlinx.protobuf)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.jetbrains.kotlin.test)

}

application {
    mainClass.set("application.MainKt")
}
