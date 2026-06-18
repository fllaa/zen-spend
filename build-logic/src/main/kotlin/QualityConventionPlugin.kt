import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class QualityConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jlleitschuh.gradle.ktlint")
        pluginManager.apply("io.gitlab.arturbosch.detekt")

        extensions.configure<DetektExtension> {
            config.setFrom(rootProject.files("config/detekt/detekt.yml"))
            buildUponDefaultConfig = true
            parallel = true
        }
    }
}
