import de.undercouch.gradle.tasks.download.Download
import flatbuffers.language.currentHostTarget

plugins {
  id("flatbuffers.language.kotlin-multiplatform")

  id("org.jetbrains.kotlin.plugin.allopen") version "1.6.0"
  id("org.jetbrains.kotlinx.benchmark") version "0.3.1"
  id("io.morethan.jmhreport") version "0.9.0"
  id("de.undercouch.download")
}

group = "com.google.flatbuffers.kotlin"
version = "2.0.0-SNAPSHOT"

kotlin {
  explicitApi()

  jvm {
    compilations.all {
      kotlinOptions {
        jvmTarget = flatbuffers.DependencyVersions.java
      }
    }
    testRuns["test"].executionTask.configure {
      useJUnitPlatform()
    }
  }

  currentHostTarget {
    binaries {
//      executable {
//        entryPoint = "main"
//      }
    }
  }

  js {
    browser {
      binaries.executable()
      testTask {
        useKarma {
          useChromeHeadless()
        }
      }
    }
  }

//  macosX64()
//  iosArm32()
//  iosArm64()
//  iosX64()

  sourceSets {

    all {
//      languageSettings.enableLanguageFeature("InlineClasses")
      languageSettings.optIn("kotlin.ExperimentalUnsignedTypes")
    }

    val commonMain by getting {
      dependencies {
        implementation(kotlin("stdlib-common"))
      }
    }
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
      }
    }

    val jvmMain by getting {
      dependencies {
        implementation(project(":language-ports:java"))
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.4.1")
      }
    }
    val jvmTest by getting {
      dependencies {
        implementation(kotlin("test-junit5"))

        implementation(project(":tests")) {
          because("The 'tests' project provides Flatbuffers test resource files")
        }

        implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.3.0")
        implementation("com.squareup.moshi:moshi-kotlin:1.11.0")
        implementation("com.google.code.gson:gson:2.8.5")
      }
    }

    val jsMain by getting {
      dependsOn(commonMain)
    }
    val jsTest by getting {
      dependsOn(commonTest)
      dependencies {
        implementation(kotlin("test-js"))
      }
    }

//    val nativeMain by creating { dependsOn(commonMain) }
//    val nativeTest by creating { dependsOn(commonMain) }
//
//    val macosX64Main by getting { dependsOn(nativeMain) }
//    val iosArm32Main by getting { dependsOn(nativeMain) }
//    val iosArm64Main by getting { dependsOn(nativeMain) }
//    val iosX64Main by getting { dependsOn(nativeMain) }

    /* Targets configuration omitted.
     *  To find out how to configure the targets, please follow the link:
     *  https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#setting-up-targets */
    targets {
      targetFromPreset(presets.getAt("jvm"))
      targetFromPreset(presets.getAt("js"))
//      targetFromPreset(presets.getAt("macosX64"))
//      targetFromPreset(presets.getAt("iosArm32"))
//      targetFromPreset(presets.getAt("iosArm64"))
//      targetFromPreset(presets.getAt("iosX64"))
    }
//    dependencies {
//      implementation(kotlin("stdlib-common"))
//      implementation(project(":flatbuffers-kotlin"))
//      implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
//      implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.4.1")
//    }
  }
}

val downloadBenchmarkData by tasks.registering(Download::class) {
  group = LifecycleBasePlugin.BUILD_GROUP
  description = "Download JSON files for benchmarking ${project.name}"
  // We are downloading json benchmark samples from serdes-rs project.
  // see: https://github.com/serde-rs/json-benchmark/blob/master/data
  val baseUrl = "https://github.com/serde-rs/json-benchmark/raw/master/data"
  src(
    listOf(
      "$baseUrl/canada.json",
      "$baseUrl/twitter.json",
      "$baseUrl/citm_catalog.json",
    )
  )
  dest(project.layout.projectDirectory.dir("src/jvmMain/resources").asFile)
  overwrite(false)
}

project.tasks.assemble { dependsOn(downloadBenchmarkData) }

// allOpen plugin is needed for the benchmark annotations.
// for more information, see https://github.com/Kotlin/kotlinx-benchmark#gradle-plugin
allOpen {
  annotation("org.openjdk.jmh.annotations.State")
}

// This plugin generates a static html page with the aggregation
// of all benchmarks ran. very useful visualization tool.
jmhReport {
  val baseFolder = project.file("build/reports/benchmarks/main").absolutePath
  val lastFolder = project.file(baseFolder).list()?.sortedArray()?.lastOrNull() ?: ""
  jmhResultPath = "$baseFolder/$lastFolder/jvm.json"
  jmhReportOutput = "$baseFolder/$lastFolder"
}

// For now, we benchmark on JVM only
benchmark {
  configurations.getByName("main") {
    iterations = 5
    iterationTime = 300
    iterationTimeUnit = "ms"
    // uncomment for benchmarking JSON op only
    // include(".*JsonBenchmark.*")
  }
  targets {
    register("jvm")
    register("js")
  }
}
