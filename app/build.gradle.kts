plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "cl.duoc.tutorvirtual"

    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "cl.duoc.tutorvirtual.planb"

        minSdk = 24
        targetSdk = 37

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf(
                    "androidx.**",
                    "kotlin.**",
                    "kotlinx.**"
                )
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}

dependencies {

    // JETPACK COMPOSE
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.compose.ui)

    implementation(libs.androidx.compose.ui.graphics)

    implementation(libs.androidx.compose.ui.tooling.preview)

    // ANDROID
    implementation(libs.androidx.core.ktx)

    // LIFECYCLE
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // ARQUITECTURA MVVM
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // ALMACENAMIENTO LOCAL DATASTORE
    implementation(
        "androidx.datastore:datastore-preferences:1.1.7"
    )

    // PRUEBAS UNITARIAS
    testImplementation(libs.junit)

    // PRUEBAS INSTRUMENTADAS
    androidTestImplementation(
        platform(libs.androidx.compose.bom)
    )

    androidTestImplementation(
        libs.androidx.compose.ui.test.junit4
    )

    androidTestImplementation(
        libs.androidx.espresso.core
    )

    androidTestImplementation(
        libs.androidx.junit
    )

    // HERRAMIENTAS DE DESARROLLO
    debugImplementation(
        libs.androidx.compose.ui.test.manifest
    )

    debugImplementation(
        libs.androidx.compose.ui.tooling
    )
}
