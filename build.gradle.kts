import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform")
    id("org.jetbrains.changelog") version "2.2.1"
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

val kotlinPoetVersion = "2.0.0"
val gsonVersion = "2.8.9"
dependencies {

    implementation("com.squareup:kotlinpoet:$kotlinPoetVersion")
    implementation("com.google.code.gson:gson:$gsonVersion")

    intellijPlatform {
//        intellijIdeaCommunity("2024.3.1.1")
        local("C:\\Program Files\\JetBrains\\IntelliJ IDEA Community Edition 2024.3.1")

        bundledPlugins(providers.gradleProperty("platformBundledPlugins").map { it.split(',') })

        plugins(providers.gradleProperty("platformPlugins").map { it.split(',') })

        jetbrainsRuntime()
        instrumentationTools()
        pluginVerifier()
        zipSigner()
        testFramework(TestFrameworkType.Platform)
    }
}

intellijPlatform {
    buildSearchableOptions = true
    instrumentCode = true
    projectName = project.name

    pluginConfiguration {
        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
        }
    }

    signing {
        certificateChainFile = file("chain.crt")
        privateKeyFile = file("private.pem")
        password = "admin123"
    }

    publishing {
        token = System.getenv("PUBLISH_TOKEN")
    }
}

changelog {
    groups.empty()
    repositoryUrl = providers.gradleProperty("pluginRepositoryUrl")
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
    wrapper {
        gradleVersion = providers.gradleProperty("gradleVersion").get()
    }

    patchPluginXml {
        sinceBuild = providers.gradleProperty("pluginSinceBuild")
        untilBuild = provider { null }

//        changeNotes.set(provider {
//            val version = project.version.toString() // Get the version from the project
//            val changelogItem = changelog.get(version) // Get the changelog item for that version
//            val rendered = changelog.renderItem(
//                changelogItem,
//                Changelog.OutputType.HTML
//            )
//            println("Rendered Change Notes: $rendered")
//            rendered
//        })
    }

    publishPlugin {
        dependsOn(patchChangelog)
    }
    buildSearchableOptions {
        enabled = false
    }
}
