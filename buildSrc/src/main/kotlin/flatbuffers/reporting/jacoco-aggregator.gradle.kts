package flatbuffers.reporting


plugins {
  base
  jacoco
}

description = """
    Gathers JaCoCo reports from projects and aggregates them into a single report. 
    Use in `dependencies { jacocoAggregateSource(<project>) }` to the subprojects to aggregate.
    """.trimIndent()

// The approach here is inspired by this sample
// https://docs.gradle.org/current/samples/sample_jvm_multi_project_with_code_coverage.html

jacoco {
  toolVersion = "0.8.7"
}

val jacocoAggregateSource: Configuration by configurations.creating {
  description = "A resolvable configuration to collect JaCoCo report information from projects"
  isVisible = false
  isCanBeResolved = true
  isCanBeConsumed = false
}

val sourcesPath: Configuration by configurations.creating {
  description = "A resolvable configuration to collect JaCoCo source directories"
  isVisible = false
  isCanBeResolved = true
  isCanBeConsumed = false
  extendsFrom(jacocoAggregateSource)
  attributes { jacocoSourceDirs(project.objects) }
}

val classesDirs: Configuration by configurations.creating {
  description = "A resolvable configuration to collect JaCoCo class directories"
  isVisible = false
  isCanBeResolved = true
  isCanBeConsumed = false
  extendsFrom(jacocoAggregateSource)
  attributes { jacocoClassDirs(project.objects) }
}

val coverageDataPath: Configuration by configurations.creating {
  description = "A resolvable configuration to collect JaCoCo coverage data"
  isVisible = false
  isCanBeResolved = true
  isCanBeConsumed = false
  extendsFrom(jacocoAggregateSource)
  attributes { jacocoCoverageData(project.objects) }
}

val jacocoAggregatedReport by tasks.registering(JacocoReport::class) {
  group = LifecycleBasePlugin.VERIFICATION_GROUP
  description = "Combines code coverage from all 'jacocoAggregateSource' projects"

  additionalSourceDirs(
    sourcesPath
      .incoming
      .artifactView { lenient(true) }
      .files
  )

  additionalClassDirs(
    classesDirs
      .incoming
      .artifactView { lenient(true) }
      .files
  )

  executionData(
    coverageDataPath
      .incoming
      .artifactView { lenient(true) }
      .files
      .filter { it.exists() }
  )

  reports {
    xml.required.set(true)
    html.required.set(true)
  }
  val htmlReportLocation = reports.html.outputLocation.locationOnly
    .map { it.asFile.resolve("index.html").invariantSeparatorsPath }

  doLast {
    logger.lifecycle(
      """
        Created aggregated JaCoCo report from:
        classDirectories      ${classDirectories.distinct().joinToString()}
        additionalSourceDirs  ${additionalSourceDirs.distinct().joinToString()} 
        additionalClassDirs   ${additionalClassDirs.distinct().joinToString()}  
        executionData         ${executionData.distinct().joinToString()}        
      """.trimIndent()
    )

    logger.lifecycle("Jacoco combined report: ${htmlReportLocation.get()}")
  }

}

// Make JaCoCo aggregate report generation part of the 'check' lifecycle phase
tasks.check {
  dependsOn(jacocoAggregatedReport, jacocoAggregateSource)
}
