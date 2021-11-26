import org.gradle.jvm.tasks.Jar

plugins {
  id("flatbuffers.base")
  `java-library`
}

tasks.named<Jar>("jar").configure {
  from(project.layout.projectDirectory) {
    exclude("build/**")
    include("**/*.mon", "**/*.fbs", "**/*.golden")
  }
  includeEmptyDirs = false
}
