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
        versionCode = 3
        versionName = "1.3.0"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
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

publishing {
    publications {
        create<MavenPublication>("release") {
            from(components["release"])
            groupId = "com.github.theRockAkash" // Your group ID (typically your domain)
            artifactId = "ShakeTraceCompose" // Your library name
            version = "1.3.0" // Library version (this should match the tag)

            pom {
                name.set("ShakeTraceCompose")
                description.set("An Android Compose library for logging network requests and responses. Also enables shake to view logs.")
                url.set("https://github.com/theRockAkash/shaketrace.compose")
                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
                developers {
                    developer {
                        id.set("theRockAkash")
                        name.set("Akash")
                        email.set("therockakash77@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/theRockAkash/shaketrace.compose.git")
                    developerConnection.set("scm:git:https://github.com/theRockAkash/shaketrace.compose.git")
                    url.set("https://github.com/theRockAkash/shaketrace.compose")
                }
            }
        }
    }
    repositories {
        maven {
            url = uri("https://jitpack.io")
        }
    }
}
