/*
 * Copyright 2021 Google Inc. All rights reserved.
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
package com.google.flatbuffers.kotlin

import kotlin.test.Test
import kotlin.test.assertEquals

class JSONJsTest {

  class SampleJson {

    /**
     * Kotlin `Double.toString()` produce different strings depending on the platform,
     * so on `JVM` it is `1.2E33`, while on `js` it is `1.2e+33`
     *
     * @see [JSONTest.SampleJson.parseSample]
     */
    @Test
    fun parseSample() {

      val data = JSONTest.SampleJson.dataStr.encodeToByteArray()
      val root = JSONParser().parse(ArrayReadWriteBuffer(data, writePosition = data.size))

      assertEquals(
        //language=JSON
        """{"ary":[1,2,3],"boolean_false":false,"boolean_true":true,"double":1.2e+33,"hello":"world","interesting":"value","null_value":null,"object":{"field1":"hello"}}""",
        root.toJson()
      )
    }
  }

}
