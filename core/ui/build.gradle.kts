plugins {
    id("com.flla.zenspend.android.library")
    id("com.flla.zenspend.android.compose")
    id("com.flla.zenspend.quality")
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
}
