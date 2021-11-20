package flatbuffers.language

plugins {
  id("flatbuffers.base")
  `java-library`
}


java {
  withSourcesJar()
  withJavadocJar()
}

val projectJavaVersion = JavaLanguageVersion.of(flatbuffers.DependencyVersions.java)


java {
  toolchain {
    languageVersion.set(projectJavaVersion)
  }
}

tasks.withType<JavaCompile> {
  options.encoding = "UTF-8"
}

tasks.test {
  useJUnitPlatform()
  testLogging {
    events("passed", "skipped", "failed")
  }
}
