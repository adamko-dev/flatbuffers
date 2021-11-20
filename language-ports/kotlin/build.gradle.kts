import org.jetbrains.kotlin.ir.backend.js.compile

plugins {
  kotlin("multiplatform") version "1.4.20"
  id("org.jetbrains.kotlin.plugin.allopen") version "1.4.20"
  id("org.jetbrains.kotlinx.benchmark") version "0.3.0"
  id("io.morethan.jmhreport") version "0.9.0"
  id("de.undercouch.download") version "4.1.1"
}

group = "com.google.flatbuffers.kotlin"
version = "2.0.0-SNAPSHOT"

kotlin {
  explicitApi()
  jvm()
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
  macosX64()
  iosArm32()
  iosArm64()
  iosX64()

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(kotlin("stdlib-common"))
      }



kotlin {
  jvm {
    withJava()
    compilations.all {
      kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
      }
    }
  }

  sourceSets {

    all {
      languageSettings.enableLanguageFeature("InlineClasses")
      languageSettings.useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
    }

    val commonTest by getting {
      dependencies {
        implementation(kotlin("test-common"))
        implementation(kotlin("test-annotations-common"))
      }
    }
    val jvmTest by getting {
      dependencies {
        implementation(kotlin("test-junit"))
      }
    }
    val jvmMain by getting {
      kotlin.srcDir("java")
      dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.4.1")
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
    val nativeMain by creating {
        dependsOn(commonMain)
    }
    val nativeTest by creating {
      dependsOn(commonMain)
    }
    val macosX64Main by getting {
      dependsOn(nativeMain)
    }

    val iosArm32Main by getting {
      dependsOn(nativeMain)
    }
    val iosArm64Main by getting {
      dependsOn(nativeMain)
    }
    val iosX64Main by getting {
      dependsOn(nativeMain)
    }

    all {
      languageSettings.enableLanguageFeature("InlineClasses")
      languageSettings.useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
    }
  }

  /* Targets configuration omitted.
   *  To find out how to configure the targets, please follow the link:
   *  https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#setting-up-targets */
  targets {
    targetFromPreset(presets.getAt("jvm"))
    targetFromPreset(presets.getAt("js"))
    targetFromPreset(presets.getAt("macosX64"))
    targetFromPreset(presets.getAt("iosArm32"))
    targetFromPreset(presets.getAt("iosArm64"))
    targetFromPreset(presets.getAt("iosX64"))
  }
      dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.3.0")
        implementation(kotlin("stdlib-common"))
        implementation(project(":flatbuffers-kotlin"))
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.4.1")

        //moshi
        implementation("com.squareup.moshi:moshi-kotlin:1.11.0")

        //gson
        implementation("com.google.code.gson:gson:2.8.5")
      }
    }

    /* Targets configuration omitted.
     *  To find out how to configure the targets, please follow the link:
     *  https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#setting-up-targets
     */
    targets {
      targetFromPreset(presets.getAt("jvm"))
    }
  }
}

// This task download all JSON files used for benchmarking
tasks.register<de.undercouch.gradle.tasks.download.Download>("downloadMultipleFiles") {
  // We are downloading json benchmark samples from serdes-rs project.
  // see: https://github.com/serde-rs/json-benchmark/blob/master/data
  val baseUrl = "https://github.com/serde-rs/json-benchmark/raw/master/data/"
  src(listOf("$baseUrl/canada.json", "$baseUrl/twitter.json", "$baseUrl/citm_catalog.json"))
  dest(File("${project.projectDir.absolutePath}/src/jvmMain/resources"))
  overwrite(false)
}

project.tasks.named("compileKotlinJvm") {
  dependsOn("downloadMultipleFiles")
}


// allOpen plugin is needed for the benchmark annotations.
// for more infomation, see https://github.com/Kotlin/kotlinx-benchmark#gradle-plugin
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

// For now we benchmark on JVM only
benchmark {
  configurations {
    this.getByName("main") {
      iterations = 5
      iterationTime = 300
      iterationTimeUnit = "ms"
      // uncomment for benchmarking JSON op only
      // include(".*JsonBenchmark.*")
    }
  }
  targets {
    register("jvm")
  }
}
