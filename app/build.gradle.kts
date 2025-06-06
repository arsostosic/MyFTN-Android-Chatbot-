plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.myftn"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myftn"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }



}

dependencies {
    //implementation ("com.knowledgegraph.langchain:text-splitter:1.0.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.10.0")
    implementation ("org.jsoup:jsoup:1.15.4")
    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation ("org.mockito:mockito-core:4.2.0")
    androidTestImplementation ("org.mockito:mockito-android:4.2.0")
    androidTestImplementation ("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestImplementation ("androidx.test.espresso:espresso-intents:3.5.1")
    testImplementation ("com.squareup.okhttp3:mockwebserver:4.9.0")


}