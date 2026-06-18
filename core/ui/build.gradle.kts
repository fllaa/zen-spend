plugins {
    id("com.flla.example.android.library")
    id("com.flla.example.android.compose")
    id("com.flla.example.quality")
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
}
