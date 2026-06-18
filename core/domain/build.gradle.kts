plugins {
    id("com.flla.zenspend.kotlin.library")
    id("com.flla.zenspend.quality")
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
}
