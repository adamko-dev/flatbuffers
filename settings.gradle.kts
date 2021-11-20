rootProject.name = "flatbuffers"

apply(from = "./gradle/shared-build-config/dependency-management.settings.gradle.kts")

include(
  ":language-ports:java",
  ":language-ports:java-grpc",

  ":language-ports:kotlin",
)

// the Android app can be included - but its config is a little outdated
// and doesn't work on Windows ("CMake Error ... add_library ... Invalid character escape '\U'.")
//includeBuild("./android")
