plugins {
  idea
  `kotlin-dsl`
  kotlin("jvm") version "1.6.0"
}

dependencies {
  val kotlinVersion = "1.6.0"
  implementation(platform("org.jetbrains.kotlin:kotlin-bom:$kotlinVersion"))
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")
}


val projectJvmTarget = "11"

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {

  kotlinOptions {
    jvmTarget = projectJvmTarget
    apiVersion = "1.6"
    languageVersion = "1.6"
  }

  kotlinOptions.freeCompilerArgs += listOf(
    "-Xopt-in=kotlin.OptIn",
    "-Xopt-in=kotlin.RequiresOptIn",
    "-Xopt-in=kotlin.ExperimentalStdlibApi",
    "-Xopt-in=kotlin.time.ExperimentalTime",
    "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
    "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi",
  )
}


kotlin {
  jvmToolchain {
    (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(projectJvmTarget))
  }

  kotlinDslPluginOptions {
    jvmTarget.set(projectJvmTarget)
  }
}

idea {
  module {
    isDownloadSources = true
    isDownloadJavadoc = true
  }
}
