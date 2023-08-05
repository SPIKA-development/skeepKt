plugins {
    application
}

apply(plugin = "kotlin")
apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

version = ""

dependencies {
    add("implementation", project(":shared"))
    add("implementation", libs.ktor.server.netty)
    add("implementation", libs.ktor.server.core)
    add("implementation", libs.ktor.server.websockets)
    add("implementation", libs.logback)
    implementation(rootProject.fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation("com.oracle.database.jdbc:ojdbc11:23.2.0.0")
    implementation("com.oracle.database.security:oraclepki:21.1.0.0")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    implementation(libs.ktor.server.caching.headers)
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
    implementation(libs.ktor.network.tls)

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

@Suppress("UnstableApiUsage")
tasks.withType<ProcessResources> {
    from(File(rootProject.projectDir, "./client/build/distributions").absolutePath)
}