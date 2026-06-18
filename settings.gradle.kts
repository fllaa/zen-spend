pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "KotlinAndroidBoilerplate"

include(":app")
include(":core:common")
include(":core:designsystem")
include(":core:network")
include(":core:database")
include(":core:datastore")
include(":core:domain")
include(":core:model")
include(":core:testing")
include(":core:ui")
include(":feature:auth")
include(":feature:home")
include(":feature:settings")
include(":feature:profile")
