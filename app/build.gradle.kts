import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.ourproject"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.ourproject"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localPropertiesFile = rootProject.file("local.properties")
        val apiKey = if (localPropertiesFile.exists()) {
            val props = Properties()
            localPropertiesFile.inputStream().use { props.load(it) }
            val key = props.getProperty("WEATHER_API_KEY") ?: ""
            println("App: Loaded API key from local.properties: ${if (key.isNotEmpty()) "${key.take(5)}..." else "EMPTY"}")
            key
        } else {
            println("App: local.properties file not found!")
            ""
        }
        if (apiKey.isEmpty()) {
            println("App: WARNING - API key is empty! BuildConfig.WEATHER_API_KEY will be empty string.")
        }
        buildConfigField("String", "WEATHER_API_KEY", "\"$apiKey\"")
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

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")

    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.gridlayout:gridlayout:1.0.0")

    implementation("androidx.navigation:navigation-fragment-ktx:2.8.2")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.2")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.6")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")

    implementation("io.coil-kt:coil:2.7.0")
    implementation("io.coil-kt:coil-base:2.7.0")
    implementation("io.coil-kt:coil-gif:2.7.0")
    implementation("io.coil-kt:coil-svg:2.7.0")

    implementation(project(":core"))
    implementation(project(":data"))
    implementation(project(":libs:network"))
    implementation(project(":features:current"))
    implementation(project(":features:forecast"))
    implementation(project(":features:locations"))

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}