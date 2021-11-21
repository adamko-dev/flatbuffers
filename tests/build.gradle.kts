plugins {
  id("flatbuffers.base")
//  distribution
  `java-library`
}

sourceSets {
  main {
    java.setSrcDirs(emptyList<String>())
    resources {
      setSrcDirs(listOf(project.projectDir))
      exclude("build/**")
      include("**/*.mon", "**/*.fbs", "**/*.golden")
    }
  }
  test {
    java.setSrcDirs(emptyList<String>())
    resources.setSrcDirs(emptyList<String>())
  }
}

tasks.withType<Copy> {
  includeEmptyDirs = false
}

idea {
  module {
    // IntelliJ doesn't like that the resource directory is the same as the 'main' directory,
    // so remove the main directory.
    // TODO store test data in a separate directory
//    sourceDirs.clear()
   // contentRoot = file("dummy")
  }
}

//distributions.main {
//  contents {
//    from(layout.projectDirectory) {
//      include(
//        "**/*.mon",
//        "**/*.fbs",
//        "**/*.golden",
//      )
//    }
//    exclude("build/**")
//    includeEmptyDirs = false
//  }
//}


//val flatbuffersTestDataDist: Distribution by distributions.creating {
//}

//flatbuffersTestData


//
//val testTask by tasks.creating(Sync::class) {
//  group = "AAAAA"
//  from(layout.projectDirectory) {
//    include(
//      "**/*.mon",
//      "**/*.fbs",
//    )
//  }
//  exclude("build/**")
//
//  into(temporaryDir)
//  includeEmptyDirs = false
//
//  doFirst {
//    delete(temporaryDir)
//    mkdir(temporaryDir)
//  }
//}


//
//val serialisedFiles: Configuration by configurations.creating {
//  isVisible = false
//  isCanBeResolved = false
//  isCanBeConsumed = true
//
//  fileTree(project.rootDir) {
//    include("*.mon")
//  }
//    .forEach {
//      outgoing.artifact(it)
//    }
//}
//
//val flatbuffersTestData: Configuration by configurations.creating {
//  isVisible = false
//  isCanBeResolved = false
//  isCanBeConsumed = true

//  layout.projectDirectory.asFileTree.matching {
//    include("*.fbs")
//  }
//
//  fileTree(project.rootDir) {
//    include("*.fbs")
//  }
//    .forEach {
//      outgoing.artifact(it)
//    }

//  fileTree(project.projectDir) {
//    include("**/*.mon", "**/*.fbs", "**/*.golden")
//    exclude("build/**")
//  }
//    .forEach { outgoing.artifact(it) }

//  flatbuffersTestDataDist.contents.
//
//  outgoing.artifact(tasks.flatbuffersTestDataDistZip) {
//    builtBy(tasks.flatbuffersTestDataDistZip)
//  }
//
//}
//
//dependencies {
//  flatbuffersTestData(fileTree(project.projectDir) {
//    include("**/*.mon", "**/*.fbs", "**/*.golden")
//    exclude("build/**")
//  })
//
//  flatbuffersTestData(files(layout.projectDirectory.asFileTree.matching {
//    include("**/*.mon", "**/*.fbs", "**/*.golden")
//    exclude("build/**")
//  }))
//
//  flatbuffersTestData(project.tasks.distZip)
//}
