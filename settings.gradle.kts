rootProject.name = "flatbuffers"

apply(from = "./gradle/shared-build-config/dependency-management.settings.gradle.kts")

include(":language-ports:java")
include(":language-ports:java-grpc")
