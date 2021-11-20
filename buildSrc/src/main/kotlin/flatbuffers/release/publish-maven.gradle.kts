package flatbuffers.release

plugins {
  `maven-publish`
}


publishing {
  publications.create<MavenPublication>("maven") {
    from(components["java"])
  }
}
