pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            credentials {
                username = "mapbox"
                password = "pk.eyJ1IjoiZG90aWVudGhhbmgiLCJhIjoiY21jOHpwZzN3MW5haDJsc2Frcm0zbDFmMiJ9.GxpLaPuqleM_4qUVq9EEww" // 👈 Thay bằng token thật
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}




rootProject.name = "Android Booking Application Project"
include(":app")
