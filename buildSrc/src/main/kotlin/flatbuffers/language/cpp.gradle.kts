package flatbuffers.language

plugins {
  id("flatbuffers.base")
}

idea {
  module {
    sourceDirs.addAll(
      listOf(
        file("$projectDir/src/main/cpp"),
        file("$projectDir/src/main/headers"),
        file("$projectDir/src/main/public"),
      )
    )
  }
}
