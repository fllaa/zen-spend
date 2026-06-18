plugins {
    id("com.flla.zenspend.kotlin.library")
    id("com.flla.zenspend.quality")
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
