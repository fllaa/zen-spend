plugins {
    id("com.flla.zenspend.android.library")
    id("com.flla.zenspend.android.hilt")
    id("com.flla.zenspend.quality")
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
