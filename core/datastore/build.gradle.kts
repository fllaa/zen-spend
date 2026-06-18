plugins {
    id("com.flla.example.android.library")
    id("com.flla.example.android.hilt")
    id("com.flla.example.quality")
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)
}
