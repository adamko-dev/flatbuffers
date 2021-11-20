package flatbuffers.langugage

plugins {
  id("flatbuffers.base")
  `java-library`
}


java {
  withSourcesJar()
  withJavadocJar()
}

val projectJavaVersion = JavaVersion.VERSION_1_8


java.sourceCompatibility = projectJavaVersion

java {
  toolchain {
//    languageVersion.set(projectJavaVersion)
  }
}

tasks.withType<JavaCompile>() {
  options.encoding = "UTF-8"
}
