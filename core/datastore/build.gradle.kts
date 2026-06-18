plugins {
    id("com.flla.zenspend.android.library")
    id("com.flla.zenspend.android.hilt")
    id("com.flla.zenspend.quality")
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)
}
