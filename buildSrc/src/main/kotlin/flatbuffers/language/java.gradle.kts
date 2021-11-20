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

tasks.withType<Javadoc> { //configureEach is not needed
  isFailOnError = false
  options {
    encoding = "UTF-8"

    // unsafe cast - https://github.com/gradle/gradle/issues/7038#issuecomment-448294937
    this as StandardJavadocDocletOptions
    addStringOption("Xdoclint:none", "-quiet")
  }
}
