pluginManagement { repositories {  mavenLocal(); mavenCentral(); google(); gradlePluginPortal()  }  }

plugins {
    id("com.soywiz.kproject.settings") version "0.3.2-de-1.9.20-2914"
}

kproject("./deps")

include(":shared")
include(":client")
include(":server")
