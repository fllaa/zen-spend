plugins {
    id("com.flla.example.kotlin.library")
    id("com.flla.example.quality")
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:model"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.turbine)
    implementation(libs.junit)
}
