plugins {
  idea
  `project-report`
  id("flatbuffers.reporting.jacoco-aggregator")
}

group = "com.google.flatbuffers"
version = "2.0.3"
description = "FlatBuffers: Memory Efficient Serialization Library"

dependencies {
  subprojects.forEach {
    logger.info("root project depends on ${it.displayName} for Jacoco report")
    jacocoAggregateSource(it)
  }
}

tasks.wrapper {
  gradleVersion = "7.3.3"
  distributionType = Wrapper.DistributionType.ALL
}
