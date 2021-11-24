package flatbuffers.language


plugins {
  kotlin("multiplatform")
//  id("flatbuffers.linter.kotlin")

  id("flatbuffers.reporting.jacoco")
}

val jacocoTestReport by tasks.registering(JacocoReport::class) {
  group = LifecycleBasePlugin.VERIFICATION_GROUP
  description = "JaCoCo test coverage for a Kotlin Multiplatform project"

  executionData.from(
    layout.buildDirectory.file("jacoco/jvmTest.exec")
  )
  classDirectories.from("$buildDir/classes/kotlin/jvm/main")
  sourceDirectories.from(
    "$projectDir/src/commonMain/kotlin",
    "$projectDir/src/jvmMain/kotlin",
  )

}
