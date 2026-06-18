plugins {
    `kotlin-dsl`
}

group = "com.flla.example.buildlogic"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.kotlin.compose.gradle.plugin)
    compileOnly(libs.kotlinx.serialization.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
    compileOnly(libs.hilt.gradle.plugin)
    compileOnly(libs.detekt.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "com.flla.example.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "com.flla.example.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "com.flla.example.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("androidHilt") {
            id = "com.flla.example.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("kotlinLibrary") {
            id = "com.flla.example.kotlin.library"
            implementationClass = "KotlinLibraryConventionPlugin"
        }
        register("quality") {
            id = "com.flla.example.quality"
            implementationClass = "QualityConventionPlugin"
        }
    }
}
