plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.batmobile"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.batmobile"
        minSdk = 24
        targetSdk = 31
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
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.android.volley:volley:1.2.1")
    implementation ("org.osmdroid:osmdroid-android:6.1.10")
    implementation("com.google.android.gms:play-services-location:18.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.etebarian:meow-bottom-navigation:1.2.0")
    implementation ("com.google.code.gson:gson:2.8.9")
    implementation ("com.makeramen:roundedimageview:2.3.0")
    implementation ("com.auth0.android:jwtdecode:2.0.2")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("rongi.rotate-layout:rotate-layout:2.0.0")
    implementation ("androidx.cardview:cardview:1.0.0")
    implementation ("com.github.blackfizz:eazegraph:1.2.5l@aar")
    implementation ("com.nineoldandroids:library:2.4.0")
    implementation ("com.github.MKergall:osmbonuspack:6.6.0")
    implementation("com.google.firebase:firebase-messaging:23.0.0")
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
}