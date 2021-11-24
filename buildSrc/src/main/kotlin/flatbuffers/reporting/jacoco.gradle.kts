package flatbuffers.reporting


plugins {
  jacoco
}

// The approach here is inspired by this sample
// https://docs.gradle.org/current/samples/sample_jvm_multi_project_with_code_coverage.html

jacoco {
  toolVersion = "0.8.7"
}

tasks.withType<JacocoReport> {
  dependsOn(tasks.withType<Test>())

  reports {
    xml.required.set(true)
    html.required.set(true)
    csv.required.set(false)
  }

  doLast {
    val htmlReportLocation = reports.html.outputLocation.locationOnly
      .map { it.asFile.resolve("index.html").invariantSeparatorsPath }

    logger.lifecycle("Jacoco report for ${project.name}: ${htmlReportLocation.get()}")
  }
}

tasks.withType<Test> {
  // report is always generated after tests run
  finalizedBy(tasks.withType<JacocoReport>())
}

val transitiveSourcesElements by configurations.registering {
  description = "Share ~source~ directories with other projects, for aggregated JaCoCo reports"
  isVisible = false
  isCanBeResolved = false
  isCanBeConsumed = true
  attributes { jacocoSourceDirs(project.objects) }

  tasks.withType<JacocoReport> {
    val jacocoReport = this
    afterEvaluate {
      (sourceDirectories + additionalSourceDirs).forEach { srcDir ->
        logger.info("${project.displayName} ||| adding sourceDir to config [${this@registering.name}] $srcDir")
        outgoing.artifact(srcDir) { builtBy(jacocoReport) }
      }
    }
  }
}

val transitiveClassElements by configurations.registering {
  description = "Share ~class~ directories with other projects, for aggregated JaCoCo reports"
  isVisible = false
  isCanBeResolved = false
  isCanBeConsumed = true
  attributes { jacocoClassDirs(project.objects) }

  tasks.withType<JacocoReport> {
    val jacocoReport = this
    afterEvaluate {
      (classDirectories + additionalClassDirs).forEach { classDir ->
        logger.info("${project.displayName} ||| adding classDir to config [${this@registering.name}] $classDir")
        outgoing.artifact(classDir) { builtBy(jacocoReport) }
      }
    }
  }
}

val coverageDataElements by configurations.registering {
  description = "Share JaCoCo coverage data to be aggregated in another project"
  isVisible = false
  isCanBeResolved = false
  isCanBeConsumed = true
  attributes { jacocoCoverageData(project.objects) }

  // This will cause the test task to run if the coverage data is requested by the aggregation task
  tasks
    .matching { it.extensions.findByType<JacocoTaskExtension>() != null }
    .all {
      val taskProvider = tasks.named(this.name)

      outgoing.artifact(
        taskProvider.map {
          val jExec = it.extensions.findByType<JacocoTaskExtension>()?.destinationFile!!
          logger.info("${project.displayName} - $taskProvider ||| registering Jacoco exec file to config [${this@registering.name}] ${jExec}")
          jExec
        }
      )
    }
}
