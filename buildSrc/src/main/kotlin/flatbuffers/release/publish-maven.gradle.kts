package flatbuffers.release

plugins {
  `maven-publish`
}


val releaseType: String? by project
val flatbuffersReleaseRepo: String? by project
val flatbuffersSnapshotRepo: String? by project


publishing {

  repositories {
    maven {

      val releaseLocation =
        when (releaseType?.lowercase()) {
          "release"  -> flatbuffersReleaseRepo ?: layout.buildDirectory.dir("repos/releases")
          "snapshot" -> flatbuffersSnapshotRepo ?: layout.buildDirectory.dir("repos/snapshots")
          else       -> layout.buildDirectory.dir("repos/local")
        }

      url = uri(releaseLocation)
    }
  }

  publications.create<MavenPublication>(project.name) {

    from(components["java"])

    pom {
      name.set("Flatbuffers :: ${project.name}")
      description.set(project.description)
      url.set("https://github.com/google/flatbuffers")
      licenses {
        license {
          name.set("Apache License V2.0")
          url.set("https://raw.githubusercontent.com/google/flatbuffers/master/LICENSE.txt")
          distribution.set("repo")
        }
      }
      developers {
        developer {
          name.set("Wouter van Oortmerssen")
        }
        developer {
          name.set("Yuri Finkelstein</name>")
          url.set("https://github.com/yfinkelstein")
        }
      }
      scm {
        url.set("https://github.com/google/flatbuffers")
        connection.set("scm:git:https://github.com/google/flatbuffers.git")
        tag.set("HEAD")
      }
      issueManagement {
        system.set("GitHub")
        url.set("https://github.com/google/flatbuffers/issues")
      }
    }
  }
}
