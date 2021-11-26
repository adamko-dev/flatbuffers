rootProject.name = "flatbuffers"

apply(from = "./gradle/shared-build-config/dependency-management.settings.gradle.kts")

include(

  ":flatc",

  ":language-ports:java",
  ":language-ports:java-grpc",
  ":language-ports:kotlin",
  ":language-ports:swift",

  ":tests",
)

// the Android app can be included - but its config is a little outdated
// and doesn't work on Windows ("CMake Error ... add_library ... Invalid character escape '\U'.")
//includeBuild("./samples/android")
