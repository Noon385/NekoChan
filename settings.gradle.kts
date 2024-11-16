pluginManagement {
    repositories {
        google()
//        mavenCentral()
        gradlePluginPortal()
        jcenter()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://jitpack.io") }

        google()

//        mavenCentral()
        jcenter()
    }
}

rootProject.name = "NekochanCoffee"
include(":app")
 