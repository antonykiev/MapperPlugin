import org.jetbrains.intellij.platform.gradle.extensions.intellijPlatform

plugins {
    id("org.jetbrains.intellij.platform.settings") version "2.1.0"
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS

    repositories {
        mavenCentral()

        intellijPlatform {
            defaultRepositories()
            jetbrainsRuntime()
            intellijDependencies()
        }
    }
}

rootProject.name = "MapperPlugin"