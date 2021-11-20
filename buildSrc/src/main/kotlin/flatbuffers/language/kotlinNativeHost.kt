package flatbuffers.language

import org.gradle.api.GradleException
import org.jetbrains.kotlin.gradle.dsl.KotlinTargetContainerWithPresetFunctions
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithHostTests


fun KotlinTargetContainerWithPresetFunctions.currentHostTarget(
  targetName: String = "native",
  configure: KotlinNativeTargetWithHostTests.() -> Unit,
): KotlinNativeTargetWithHostTests {
  val hostOs = System.getProperty("os.name")
  val isMingwX64 = hostOs.startsWith("Windows")
  val hostTarget = when {
    hostOs == "Mac OS X" -> macosX64(targetName)
    hostOs == "Linux"    -> linuxX64(targetName)
    isMingwX64           -> mingwX64(targetName)
    else                 -> throw GradleException("Preset for host OS '$hostOs' is undefined")
  }
  println("Current host target ${hostTarget.targetName}/${hostTarget.preset?.name}")
  hostTarget.configure()
  return hostTarget
}
