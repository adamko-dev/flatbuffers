package com.google.flatbuffers.kotlin

import kotlin.test.Test
import kotlin.test.assertEquals

class JSONJvmTest {

  class SampleJson {

    /**
     * Kotlin `Double.toString()` produce different strings depending on the platform,
     * so on JVM it is `1.2E33`, while on js it is `1.2e+33`
     *
     * @see [JSONTest.SampleJson.parseSample]
     */
    @Test
    fun parseSample() {

      val data = JSONTest.SampleJson.dataStr.encodeToByteArray()
      val root = JSONParser().parse(ArrayReadWriteBuffer(data, writePosition = data.size))

      assertEquals(
        //language=JSON
        """{"ary":[1,2,3],"boolean_false":false,"boolean_true":true,"double":1.2E33,"hello":"world","interesting":"value","null_value":null,"object":{"field1":"hello"}}""",
        root.toJson()
      )
    }
  }

}
