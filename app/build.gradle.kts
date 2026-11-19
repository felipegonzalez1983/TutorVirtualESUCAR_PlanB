plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)

    // Procesador para generar las clases de Room
    id("com.google.devtools.ksp") version "2.3.13"
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
                    "android.**",
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

    // ==========================================
    // JETPACK COMPOSE
    // ==========================================

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

    // ==========================================
    // ANDROID
    // ==========================================

    implementation(libs.androidx.core.ktx)

    // ==========================================
    // ARQUITECTURA MVVM
    // ==========================================

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // ==========================================
    // DATASTORE
    // ==========================================

    implementation(
        "androidx.datastore:datastore-preferences:1.1.7"
    )

    // ==========================================
    // ROOM + SQLITE
    // ==========================================

    implementation(
        "androidx.room:room-runtime:2.8.5"
    )

    implementation(
        "androidx.room:room-ktx:2.8.5"
    )

    // GENERACION DE CODIGO ROOM
    ksp(
        "androidx.room:room-compiler:2.8.5"
    )

    // ==========================================
    // AUTENTICACION BIOMETRICA
    // HUELLA DIGITAL / ROSTRO COMPATIBLE
    // ==========================================

    implementation(
        "androidx.biometric:biometric:1.1.0"
    )

    // ==========================================
    // PRUEBAS
    // ==========================================

    testImplementation(libs.junit)

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

    // ==========================================
    // HERRAMIENTAS DE DESARROLLO
    // ==========================================

    debugImplementation(
        libs.androidx.compose.ui.test.manifest
    )

    debugImplementation(
        libs.androidx.compose.ui.tooling
    )
}
