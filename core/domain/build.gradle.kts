plugins {
    id("com.flla.example.kotlin.library")
    id("com.flla.example.quality")
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
}
