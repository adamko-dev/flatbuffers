plugins {
  id("com.diffplug.spotless") version "5.8.2"
}

group = "com.google.flatbuffers"
version = "2.0.0-SNAPSHOT"


// plugin used to enforce code style
spotless {
  val klintConfig = mapOf("indent_size" to "2", "continuation_indent_size" to "2")
  kotlin {
    target("**/*.kt")
    ktlint("0.40.0").userData(klintConfig)
    trimTrailingWhitespace()
    indentWithSpaces()
    endWithNewline()
    /*
 * Copyright $YEAR Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

    licenseHeaderFile("$rootDir/spotless/spotless.kt").updateYearWithLatest(false)
    targetExclude("**/spotless.kt", "**/build/**")
  }
  kotlinGradle {
    target("*.gradle.kts")
    ktlint().userData(klintConfig)
  }
}
