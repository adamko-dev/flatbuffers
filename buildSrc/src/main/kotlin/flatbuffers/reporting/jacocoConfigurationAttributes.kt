package flatbuffers.reporting

import org.gradle.api.Named
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.AttributeContainer
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.DocsType
import org.gradle.api.attributes.Usage
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.named
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

/**
 * The attributes used to identify a [Configuration] of directories that
 * contain **source** files used to create a JaCoCo report.
 *
 * @see JacocoReport.sourceDirectories
 * @see JacocoReport.additionalSourceDirs
 * @param[objects] required to create [Named] objects
 */
fun AttributeContainer.jacocoSourceDirs(objects: ObjectFactory) {
  attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
  attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
  attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named("jacoco-source-dirs"))
}

/**
 * The attributes used to identify a [Configuration] of directories that
 * contain **class** files used to create a JaCoCo report.
 *
 * @see JacocoReport.classDirectories
 * @see JacocoReport.additionalClassDirs
 * @param[objects] required to create [Named] objects
 */
fun AttributeContainer.jacocoClassDirs(objects: ObjectFactory) {
  attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
  attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
  attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named("jacoco-class-dirs"))
}

/**
 * The attributes used to identify a [Configuration] of JaCoCo coverage report
 * data files.
 *
 * @see JacocoTaskExtension.destinationFile
 * @param[objects] required to create [Named] objects
 */
fun AttributeContainer.jacocoCoverageData(objects: ObjectFactory) {
  attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
  attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
  attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named("jacoco-coverage-data"))
}
