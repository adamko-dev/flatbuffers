package flatbuffers.language

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("flatbuffers.base")
  kotlin("jvm")
  id("flatbuffers.linter.kotlin")

  id("flatbuffers.reporting.jacoco")
}


val projectJvmTarget = "11"

tasks.withType<KotlinCompile> {

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
}

