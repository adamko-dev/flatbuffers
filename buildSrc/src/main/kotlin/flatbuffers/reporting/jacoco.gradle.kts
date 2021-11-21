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
  val htmlReportLocation = reports.html.outputLocation.locationOnly
    .map { it.asFile.resolve("index.html").invariantSeparatorsPath }

  doLast {
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
}

val transitiveClassElements by configurations.registering {
  description = "Share ~class~ directories with other projects, for aggregated JaCoCo reports"
  isVisible = false
  isCanBeResolved = false
  isCanBeConsumed = true
  attributes { jacocoClassDirs(project.objects) }
}

afterEvaluate {

  tasks.withType(JacocoReport::class.java).all {
    val srcDirs = sourceDirectories + additionalSourceDirs
    logger.error("found ${srcDirs.files.size} sourceDirectories 6656======")
    srcDirs.forEach { srcDir ->
      logger.error("${project.displayName} ||| adding sourceDir to config [${transitiveSourcesElements.name}] $srcDir")
      transitiveSourcesElements.get().outgoing.artifact(srcDir) { builtBy(this) }
    }

    val classDirs = classDirectories + additionalClassDirs
    logger.error("found ${classDirs.files.size} class directories ======")
    classDirs.forEach { classDir ->
      logger.error("${project.displayName} ||| adding classDir to config [${transitiveClassElements.name}] $classDir")
      transitiveClassElements.get().outgoing.artifact(classDir) { builtBy(this) }
    }
  }

}

configurations.register("coverageDataElements") {
  description = "Share JaCoCo coverage data to be aggregated in another project"
  isVisible = false
  isCanBeResolved = false
  isCanBeConsumed = true
  attributes { jacocoCoverageData(project.objects) }

  // This will cause the test task to run if the coverage data is requested by the aggregation task
  tasks.all {
    extensions.findByType<JacocoTaskExtension>()?.let { ext ->
      logger.error("${project.displayName} ||| adding Jacoco exec file to config [${this@register.name}] ${ext.destinationFile}")
      outgoing.artifact(ext.destinationFile!!)
    }
  }

  tasks.withType(JacocoReport::class)
    .all {
      executionData.forEach {
        logger.error("${project.displayName} ||| adding executionData to config [${this@register.name}] ${it}")
        outgoing.artifact(it) { builtBy(this) }
      }
    }
}

fun <T> mapJacocoTaskToOutput(
  getJacocoOutputs: (JacocoReport) -> Iterable<T>
): List<Pair<JacocoReport, T>> =
  tasks.withType(JacocoReport::class)
    // get some outputs for each task
    .associateWith { task -> getJacocoOutputs(task) }
    // flatten - so there's one task per output
    .flatMap { (task, outputs) -> outputs.map { task to it } }

