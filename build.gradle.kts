plugins {
  idea
  `project-report`
  id("flatbuffers.reporting.jacoco-aggregator")
}

group = "com.google.flatbuffers"
version = "2.0.3"
description = "FlatBuffers: Memory Efficient Serialization Library"

dependencies {
  subprojects.forEach { jacocoAggregateSource(it) }
}
