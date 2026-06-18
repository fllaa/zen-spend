plugins {
    `kotlin-dsl`
}

group = "com.flla.zenspend.buildlogic"

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
            id = "com.flla.zenspend.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "com.flla.zenspend.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "com.flla.zenspend.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("androidHilt") {
            id = "com.flla.zenspend.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("kotlinLibrary") {
            id = "com.flla.zenspend.kotlin.library"
            implementationClass = "KotlinLibraryConventionPlugin"
        }
        register("quality") {
            id = "com.flla.zenspend.quality"
            implementationClass = "QualityConventionPlugin"
        }
    }
}
