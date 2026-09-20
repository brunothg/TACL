import com.android.build.api.variant.FilterConfiguration
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.aboutLibraries)
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs.add("-Xannotation-default-target=param-property")
        optIn.addAll(
            "androidx.compose.material3.ExperimentalMaterial3Api",
            "androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi",
        )
    }
}

android {
    namespace = "alt.foss.brunothg.tacl"
    buildToolsVersion = "37.0.0"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "alt.foss.brunothg.tacl"
        minSdk = 29
        targetSdk = 37
        versionCode = 100
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    dependenciesInfo {
        includeInApk = false
    }

    androidResources {
        generateLocaleConfig = true
    }
    sourceSets {
        getByName(SourceSet.MAIN_SOURCE_SET_NAME) {
            res.directories.addAll(
                listOf(
                    "src/main/res-material-symbols",
                )
            )
        }
    }

}

androidComponents {
    onVariants { variant ->
        val versionName: String = android.defaultConfig.versionName ?: "unknown"
        val appName: String = rootProject.name

        variant.outputs.forEach { output ->
            val rawAbiName: String? = output.filters.find {
                it.filterType == FilterConfiguration.FilterType.ABI
            }?.identifier
            val cleanAbi: String = rawAbiName ?: "universial"
            val buildType: String = variant.buildType ?: "release"

            output.outputFileName.set("${appName}-${versionName}-${cleanAbi}-${buildType}.apk")
        }
    }
}

room {
    schemaDirectory("$projectDir/config/room/schemas")
}

aboutLibraries {
    collect {
        configPath = file("$projectDir/config/about-libraries")
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)

    implementation(libs.aboutlibraries.core)
    implementation(libs.aboutlibraries.compose)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.lifecycle.viewmodel.compose)
    ksp(libs.hilt.compiler)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.libphonenumber)

    // Test
    testImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.room.testing)

    // Debug
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

tasks {
    withType<Test> {
        useJUnitPlatform()
    }
}
