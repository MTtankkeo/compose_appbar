plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.maven.signing)
}

tasks.register("publishToMaven") {
    dependsOn("publishAllPublicationsToMavenCentralRepository")
}

val packageId = property("package.id") as String
val packageVersion = property("package.version") as String
val packageRepository = property("package.repository") as String
val packageDescription = property("package.description") as String

signing {
    sign(publishing.publications)
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    // Defines the required info of this package.
    coordinates("dev.ttangkong", packageId, packageVersion)

    // About POM
    pom {
        name = packageId
        description = packageDescription
        url = "<$packageRepository>"

        licenses {
            license {
                name = "Apache-2.0 license"
                url = "<$packageRepository/blob/main/LICENSE>"
            }
        }

        developers {
            developer {
                id = "MTtankkeo"
                name = "Ttangkong"
                email = "ttankkeo112@gmail.com"
            }
        }

        scm {
            url = packageRepository
            connection = "scm:git:github.com/MTtankkeo/$packageId.git"
            developerConnection = "scm:git:ssh://github.com:MTtankkeo/$packageId.git"
        }
    }
}

android {
    namespace = "dev.ttangkong.$packageId"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
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
}

dependencies {
    implementation(libs.androidx.core.ktx.v1120)
    implementation(libs.androidx.foundation)
}