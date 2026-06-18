import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.google.dagger.hilt.android")
        pluginManager.apply("com.google.devtools.ksp")

        dependencies {
            add("implementation", libs.library("hilt-android"))
            add("ksp", libs.library("hilt-compiler"))
            add("androidTestImplementation", libs.library("hilt-android"))
            add("kspAndroidTest", libs.library("hilt-compiler"))
            add("testImplementation", libs.library("hilt-android"))
            add("kspTest", libs.library("hilt-compiler"))
        }
    }
}
