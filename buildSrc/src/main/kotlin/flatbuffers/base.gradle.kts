package flatbuffers

plugins {
  base
  idea
  `project-report`
}

project.group = rootProject.group
project.version = rootProject.version

idea {
  module {
    isDownloadSources = true
    isDownloadJavadoc = true
  }
}
