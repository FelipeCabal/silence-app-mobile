plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
}

import java.util.Properties

// Read BASE_URL from local.properties, env var, or Gradle -P; fallback to default
val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
val baseUrlFromLocal: String? = localProps.getProperty("BASE_URL")
val baseUrlFromProject: String? = project.findProperty("BASE_URL") as String?
val baseUrlFromEnv: String? = System.getenv("BASE_URL")
val resolvedBaseUrl: String = (baseUrlFromLocal ?: baseUrlFromProject ?: baseUrlFromEnv
    ?: "https://default-backend.com/api/").let { url -> if (url.endsWith('/')) url else "$url/" }

android {
    namespace = "com.example.silenceapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.silenceapp"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField(
            "String",
            "BASE_URL",
            "\"${resolvedBaseUrl}\""
        )
    }
    
    buildFeatures {
        buildConfig = true
        compose = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    kapt {
        arguments {
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("room.incremental", "true")
            arg("room.expandProjection", "true")
        }
    }
}

dependencies {
    // --- Jetpack Compose ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation(libs.androidx.activity.compose)


    // --- Navegación Compose ---
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // --- ViewModel y Lifecycle (para MVVM y coroutines) ---
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")

    // --- Room (Base de datos local) ---
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.firebase.crashlytics.buildtools)
    kapt("androidx.room:room-compiler:2.6.1") // 🔥 Importante para generar el código
    implementation("com.google.code.gson:gson:2.10.1")

    // --- Corrutinas ---
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // --- Material Design y Splash Screen ---
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.compose.material:material-icons-extended")

    // --- Coil para cargar imágenes y SVG ---
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("io.coil-kt:coil-svg:2.6.0")

    // --- Core KTX ---
    implementation(libs.androidx.core.ktx)

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // OkHttp
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Logging interceptor para ver las peticiones en Logcat (ya incluido arriba)

    // --- Test ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
