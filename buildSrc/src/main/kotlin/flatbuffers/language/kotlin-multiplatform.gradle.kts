package flatbuffers.language

import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest


plugins {
  kotlin("multiplatform")
//  id("flatbuffers.linter.kotlin")

  id("flatbuffers.reporting.jacoco")
}

val jacocoTestReport by tasks.creating(JacocoReport::class) {
  group = LifecycleBasePlugin.VERIFICATION_GROUP
  description = "JaCoCo test coverage for a Kotlin Multiplatform project"

//  dependsOn(tasks.withType<Test>())
//  dependsOn(tasks.matching { it.name == "jvmTest" })

  additionalClassDirs(
    project.layout.buildDirectory.files("classes/kotlin/jvm")
  )
  additionalClassDirs(
    file("$buildDir/classes/kotlin/jvm")
  )
  classDirectories.from(
    project.layout.buildDirectory.files("classes/kotlin/jvm")
  )
  classDirectories.from(
    file("$buildDir/classes/kotlin/jvm")
  )
  sourceDirectories.from(
    files(
      "$projectDir/src/commonMain/kotlin",
      "$projectDir/src/commonTest/kotlin",
      "$projectDir/src/jvmMain/kotlin",
      "$projectDir/src/jvmTest/kotlin",
    )
  )
  executionData.from(
    layout.buildDirectory.file("jacoco/jvmTest.exec")
  )
  logger.error("Configured $name - \\ o /")
}

tasks.matching { it.name == "jvmTest" }
  .all {
    finalizedBy(jacocoTestReport)
  }

tasks.all {
  doFirst {
    logger.error("~~~Running task ${project.name}-${name}: ${this::class}")
  }
}

tasks.findByName("jvmTest")?.apply {
  logger.error(
    "tasks.findByName   jvmTest type is ${this::class}, ${
      extensions.findByType(
        JacocoTaskExtension::class
      )
    }"
  )
  finalizedBy(jacocoTestReport)
}

tasks.withType<KotlinJvmTest> {
  logger.error(
    "tasks.withType  jvmTest type is ${this::class}, ${
      extensions.findByType(
        JacocoTaskExtension::class
      )
    }"
  )
  finalizedBy(jacocoTestReport)
}
