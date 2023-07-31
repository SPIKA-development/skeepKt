import korlibs.korge.gradle.configureAutoVersions

plugins {
    alias(libs.plugins.korge) apply false
}

configureAutoVersions()

allprojects { repositories {
    mavenLocal(); mavenCentral(); google(); gradlePluginPortal()
    maven("https://maven.pkg.github.com/cy6ergn0m/kotlinx-uuid")
} }