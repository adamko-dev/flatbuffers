plugins {
  id("flatbuffers.language.java")
}

dependencies {

  val grpcVersion = "1.36.0"
  implementation(platform("io.grpc:grpc-bom:$grpcVersion"))
  implementation("io.grpc:grpc-core")
  testImplementation("io.grpc:grpc-testing")

  val junitVersion = "5.8.1"
  testImplementation(platform("org.junit:junit-bom:$junitVersion"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher") {
    because("Only needed to run tests in a version of IntelliJ IDEA that bundles older versions")
  }

  testImplementation(project(":tests")) {
    because("The 'tests' project provides Flatbuffers test resource files")
  }

}
