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

val jacocoAggregateSource by configurations.registering {
  description = "A resolvable configuration to collect JaCoCo report information from projects"
  isVisible = false
  isCanBeResolved = true
  isCanBeConsumed = false
}

val sourcesPath by configurations.registering {
  description = "A resolvable configuration to collect JaCoCo source directories"
  isVisible = false
  isCanBeResolved = true
  isCanBeConsumed = false
  extendsFrom(jacocoAggregateSource.get())
  attributes { jacocoSourceDirs(project.objects) }
}

val classesDirs by configurations.registering {
  description = "A resolvable configuration to collect JaCoCo class directories"
  isVisible = false
  isCanBeResolved = true
  isCanBeConsumed = false
  extendsFrom(jacocoAggregateSource.get())
  attributes { jacocoClassDirs(project.objects) }
}

val coverageDataPath by configurations.registering {
  description = "A resolvable configuration to collect JaCoCo coverage data"
  isVisible = false
  isCanBeResolved = true
  isCanBeConsumed = false
  extendsFrom(jacocoAggregateSource.get())
  attributes { jacocoCoverageData(project.objects) }
}

val jacocoAggregatedReport by tasks.registering(JacocoReport::class) {
  group = LifecycleBasePlugin.VERIFICATION_GROUP
  description = "Combines code coverage from all 'jacocoAggregateSource' projects"

  mustRunAfter(tasks.withType<Test>())
  mustRunAfter(tasks.withType<JacocoReport>().matching { it != this })

  additionalSourceDirs.from(
    sourcesPath.map { conf ->
      conf.incoming.artifactView { lenient(true) }.artifacts.artifactFiles
    }
  )
  additionalClassDirs.from(
    classesDirs.map { conf ->
      conf.incoming.artifactView { lenient(true) }.artifacts.artifactFiles
    }
  )
  executionData.from(
    coverageDataPath.map { conf ->
      conf.incoming.artifactView { lenient(true) }.artifacts.artifactFiles.filter { it.exists() }
    }
  )

  reports {
    xml.required.set(true)
    html.required.set(true)
  }

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

    val htmlReportLocation = reports.html.outputLocation.locationOnly
      .map { it.asFile.resolve("index.html").invariantSeparatorsPath }

    logger.lifecycle("Jacoco combined report: ${htmlReportLocation.get()}")
  }

}

// Make JaCoCo aggregate report generation part of the 'check' lifecycle phase
tasks.check {
  dependsOn(jacocoAggregatedReport)
}
