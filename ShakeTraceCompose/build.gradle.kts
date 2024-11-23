plugins {
    id(id = "com.android.library")
    id(id = "org.jetbrains.kotlin.android")
    id(id = "org.jetbrains.kotlin.plugin.compose")
    id("maven-publish")
}

android {
    namespace = "com.therockakash.shaketrace.compose"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.material3:material3-android:1.3.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
}
afterEvaluate {

    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = "com.github.theRockAkash"
                artifactId = "ShakeTraceCompose"
                version = "1.5.0"

                from(components["release"])
            }
        }
    }

}