rootProject.name = "flatbuffers"

apply(from = "./gradle/shared-build-config/dependency-management.settings.gradle.kts")

include(
  ":language-ports:java",
  ":language-ports:java-grpc",
  
  ":language-ports:kotlin",
)
