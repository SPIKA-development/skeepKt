import korlibs.korge.gradle.configureAutoVersions

plugins {
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.korge) apply false
}

configureAutoVersions()

allprojects { repositories {
    mavenLocal(); mavenCentral(); google(); gradlePluginPortal()
    maven("https://maven.pkg.github.com/cy6ergn0m/kotlinx-uuid")
} }
