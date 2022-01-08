rootProject.name = "flatbuffers"

apply(from = "./buildSrc/repositories.settings.gradle.kts")

include(

  ":flatbuffers-compiler:app-flatc",
  ":flatbuffers-compiler:app-flathash",

  ":language-ports:java",
  ":language-ports:java-grpc",
  ":language-ports:kotlin",
  ":language-ports:swift",

  ":tests",
)

// the Android app can be included - but its config is a little outdated
// and doesn't work on Windows ("CMake Error ... add_library ... Invalid character escape '\U'.")
//includeBuild("./samples/android")
