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
import kotlin.test.assertContains
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class JSONTest {

  @Test
  fun parse2Test() {
    //language=JSON
    val dataStr = """{ "myKey" : [1, "yay"] }"""
    val data = dataStr.encodeToByteArray()
    val buffer = ArrayReadWriteBuffer(data, writePosition = data.size)
    val parser = JSONParser()
    val root = parser.parse(buffer)
    println(root.toJson())
    //language=JSON
    assertEquals("""{"myKey":[1,"yay"]}""", root.toJson())
  }

  class SampleJson {

    companion object {
      //language=JSON
      const val dataStr =
        """
          {
            "ary": [
              1,
              2,
              3
            ],
            "boolean_false": false,
            "boolean_true": true,
            "double": 1.2E33,
            "hello": "world",
            "interesting": "value",
            "null_value": null,
            "object": {
              "field1": "hello"
            }
          }
        """
    }

    /**
     * Kotlin Double.toString() produce different strings depending on the platform,
     * so on `JVM` it is `1.2E33`, while on `js` it is `1.2e+33`
     */
    @Test
    fun parseSample() {

      val data = dataStr.encodeToByteArray()
      val root = JSONParser().parse(ArrayReadWriteBuffer(data, writePosition = data.size))
      val map: Map = root.toMap()

      assertEquals(8, map.size)
      assertEquals("world", map["hello"].toString())
      assertEquals("value", map["interesting"].toString())
      assertEquals(12e32, map["double"].toDouble())
      assertContentEquals(intArrayOf(1, 2, 3), map["ary"].toIntArray())
      assertTrue(map["boolean_true"].toBoolean())
      assertFalse(map["boolean_false"].toBoolean())
      assertTrue(map["null_value"].isNull)
      assertEquals("hello", map["object"]["field1"].toString())

      val obj = map["object"]
      assertEquals(true, obj.isMap)
      //language=JSON
      assertEquals("""{"field1":"hello"}""", obj.toJson())

      // TODO Kotlin Double.toString() produce different strings depending on the platform
      // val minified = data.filterNot { it == ' '.toByte() || it == '\n'.toByte() }.toByteArray().decodeToString()
      // assertEquals(minified, root.toJson())
    }
  }

  @Test
  fun testDoubles() {
    val parser = JSONParser()

    //language=JSON
    listOf(
      // values: expected to input
      -0.0 to "-0.0",
      1.0 to "1.0",
      0.0 to "0.0",
      -0.5 to "-0.5",
      2.2250738585072014E-308 to "2.2250738585072014E-308",
      // TODO these values are slightly different on Windows
      //      1.7976931348613157 to "1.7976931348613157",
      //      3.141592653589793 to "3.141592653589793",
      //      2.718281828459045e-3 to "2.718281828459045E-3",
      //      4.9E-15 to "4.9E-15",
    ).forEach { (expected, input) ->
      val actual = parser.parse(input).toDouble()
      assertEquals(expected, actual)
    }
  }

  @Test
  fun testInts() {
    val parser = JSONParser()

    listOf(
      //pair of INPUT and EXPECTED
      "-0" to 0,
      "0" to 0,
      "-1" to -1,
      "${Int.MAX_VALUE}" to Int.MAX_VALUE,
      "${Int.MIN_VALUE}" to Int.MIN_VALUE,
    )
      .forEach { (input: String, expected: Int) ->
        val actual = parser.parse(input).toInt()
        assertEquals(expected, actual)
      }
  }

  @Test
  fun testLongs() {
    val parser = JSONParser()

    listOf<Pair<String, Long>>(
      //pair of INPUT and EXPECTED
      "-0" to 0,
      "0" to 0,
      "-1" to -1,

      // TODO Long.MAX_VALUE, Long.MIN_VALUE fails on js, browser, ChromeHeadless95, Windows 10
      //      NumberFormatException: Invalid number format: '9223372036854776000'
//      "${Long.MIN_VALUE}" to Long.MIN_VALUE,
//      "${Long.MAX_VALUE}" to Long.MAX_VALUE,
    )
      .forEach { (input: String, expected: Long) ->
        val actual: Long = parser.parse(input).toLong()
        assertEquals(expected, actual)
      }
  }

  @Test
  fun testBooleans() {
    val parser = JSONParser()

    assertTrue(parser.parse("true").toBoolean())
    assertFalse(parser.parse("false").toBoolean())
  }

  @Test
  fun testNull() {
    val parser = JSONParser()

    assertEquals(true, parser.parse("null").isNull)
  }

  @Test
  fun testStrings() {
    //language=JSON
    val values = arrayOf(
      """  ""                                                                                      """,
      """  "a"                                                                                     """,
      """  "hello world"                                                                           """,
      """  "\"\\\/\b\f\n\r\t cool"                                                                 """,
      """  "\u0000"                                                                                """,
      """  "\u0021"                                                                                """,
      """  "hell\u24AC\n\ro wor \u0021 ld"                                                         """,
      """  "\/_\\_\"_\uCAFE\uBABE\uAB98\uFCDE\ubcda\uef4A\b\n\r\t`1~!@#$%^&*()_+-=[]{}|;:',./<>?"  """,
    ).map { it.trim() }
    val parser = JSONParser()

    // empty
    var ref = parser.parse(values[0])
    assertEquals(true, ref.isString)
    assertEquals("", ref.toString())
    // a
    ref = parser.parse(values[1])
    assertEquals(true, ref.isString)
    assertEquals("a", ref.toString())
    // hello world
    ref = parser.parse(values[2])
    assertEquals(true, ref.isString)
    assertEquals("hello world", ref.toString())
    // "\\\"\\\\\\/\\b\\f\\n\\r\\t\""
    ref = parser.parse(values[3])
    assertEquals(true, ref.isString)
    assertEquals("\"\\/\b${12.toChar()}\n\r\t cool", ref.toString())
    // 0
    ref = parser.parse(values[4])
    assertEquals(true, ref.isString)
    assertEquals(0.toChar().toString(), ref.toString())
    // u0021
    ref = parser.parse(values[5])
    assertEquals(true, ref.isString)
    assertEquals(0x21.toChar().toString(), ref.toString())
    // "\"hell\\u24AC\\n\\ro wor \\u0021 ld\"",
    ref = parser.parse(values[6])
    assertEquals(true, ref.isString)
    assertEquals("hell${0x24AC.toChar()}\n\ro wor ${0x21.toChar()} ld", ref.toString())

    ref = parser.parse(values[7])
    println(ref.toJson())
    assertEquals(true, ref.isString)
    assertEquals("/_\\_\"_쫾몾ꮘﳞ볚\b\n\r\t`1~!@#$%^&*()_+-=[]{}|;:',./<>?", ref.toString())
  }

  @Test
  fun testUnicode() {
    // taken from test/unicode_test.json
    //language=JSON
    val data = """
      {
        "name": "unicode_test",
        "testarrayofstring": [
          "Цлїςσδε",
          "ﾌﾑｱﾑｶﾓｹﾓ",
          "フムヤムカモケモ",
          "㊀㊁㊂㊃㊄",
          "☳☶☲",
          "𡇙𝌆"
        ],
        "testarrayoftables": [
          {
            "name": "Цлїςσδε"
          },
          {
            "name": "☳☶☲"
          },
          {
            "name": "フムヤムカモケモ"
          },
          {
            "name": "㊀㊁㊂㊃㊄"
          },
          {
            "name": "ﾌﾑｱﾑｶﾓｹﾓ"
          },
          {
            "name": "𡇙𝌆"
          }
        ]
      }
    """.trimIndent()
    val parser = JSONParser()
    val ref = parser.parse(data)

    // name
    assertEquals(3, ref.toMap().size)
    assertEquals("unicode_test", ref["name"].toString())

    val testArrayOfString = ref["testarrayofstring"]
    assertEquals(6, testArrayOfString.toVector().size)
    assertEquals("Цлїςσδε", testArrayOfString[0].toString())
    assertEquals("ﾌﾑｱﾑｶﾓｹﾓ", testArrayOfString[1].toString())
    assertEquals("フムヤムカモケモ", testArrayOfString[2].toString())
    assertEquals("㊀㊁㊂㊃㊄", testArrayOfString[3].toString())
    assertEquals("☳☶☲", testArrayOfString[4].toString())
    assertEquals("𡇙𝌆", testArrayOfString[5].toString())

    val testArrayOfTables = ref["testarrayoftables"]
    assertEquals(6, testArrayOfTables.toVector().size)
    assertEquals("Цлїςσδε", testArrayOfTables[0]["name"].toString())
    assertEquals("☳☶☲", testArrayOfTables[1]["name"].toString())
    assertEquals("フムヤムカモケモ", testArrayOfTables[2]["name"].toString())
    assertEquals("㊀㊁㊂㊃㊄", testArrayOfTables[3]["name"].toString())
    assertEquals("ﾌﾑｱﾑｶﾓｹﾓ", testArrayOfTables[4]["name"].toString())
    assertEquals("𡇙𝌆", testArrayOfTables[5]["name"].toString())
  }

  @Test
  fun testArrays() {
    //language=JSON
    val values = arrayOf(
      "[]",
      "[1]",
      "[0,1, 2,3  , 4 ]",
      "[1.0, 2.2250738585072014E-308,  4.9E-320]",
      "[1.0, 2,  \"hello world\"]   ",
      "[ 1.1, 2, [ \"hello\" ] ]",
      "[[[1]]]"
    )
    val parser = JSONParser()

    // empty
    var ref = parser.parse(values[0])
    assertEquals(true, ref.isVector)
    assertEquals(0, parser.parse(values[0]).toVector().size)
    // single
    ref = parser.parse(values[1])
    assertEquals(true, ref.isTypedVector)
    assertEquals(1, ref[0].toInt())
    // ints
    ref = parser.parse(values[2])
    assertEquals(true, ref.isTypedVector)
    assertEquals(T_VECTOR_INT, ref.type)
    assertEquals(5, ref.toVector().size)
    for (i in 0..4) {
      assertEquals(i, ref[i].toInt())
    }
    // floats
    ref = parser.parse(values[3])
    assertEquals(true, ref.isTypedVector)
    assertEquals(T_VECTOR_FLOAT, ref.type)
    assertEquals(3, ref.toVector().size)
    assertEquals(1.0, ref[0].toDouble())
    assertEquals(2.2250738585072014E-308, ref[1].toDouble())
    assertEquals(4.9E-320, ref[2].toDouble())
    // mixed
    ref = parser.parse(values[4])
    assertEquals(false, ref.isTypedVector)
    assertEquals(T_VECTOR, ref.type)
    assertEquals(1.0, ref[0].toDouble())
    assertEquals(2, ref[1].toInt())
    assertEquals("hello world", ref[2].toString())
    // nester array
    ref = parser.parse(values[5])
    assertEquals(false, ref.isTypedVector)
    assertEquals(T_VECTOR, ref.type)
    assertEquals(1.1, ref[0].toDouble())
    assertEquals(2, ref[1].toInt())
    assertEquals("hello", ref[2][0].toString())
  }

  /**
   * Several test cases provided by json.org
   * For more details, see: [https://json.org/JSON_checker/](https://json.org/JSON_checker/) with only
   * one exception: single strings are considered accepted, whereas in
   * the test suite it should fail.
   */
  @Test
  fun testParseMustFail() {
    listOf(
      //   invalid json                                                    // expected error message fragment
      """  ["Unclosed array"                                           """ to "Error At",
      """  {unquoted_key: "keys must be quoted"}                       """ to "Error At",
      """  ["extra comma",]                                            """ to "Error At",
      """  ["double extra comma",,]                                    """ to "Error At",
      """  [   , "<-- missing value"]                                  """ to "Error At",
      """  ["Comma after the close"],                                  """ to "Error At",
      """  ["Extra close"]]                                            """ to "Error At",
      """  {"Extra comma": true,}                                      """ to "Error At",
      """  {"Extra value after close": true} "misplaced quoted value"  """ to "Error At",
      """  {"Illegal expression": 1 + 2}                               """ to "Error At",
      """  {"Illegal invocation": alert()}                             """ to "Error At",
      """  {"Numbers cannot have leading zeroes": 013}                 """ to "Error At",
      """  {"Numbers cannot be hex": 0x14}                             """ to "Error At",
      """  ["Illegal backslash escape: \x15"]                          """ to "Error At",
      """  [\naked]                                                    """ to "Error At",
      """  ["Illegal backslash escape: \017"]                          """ to "Error At",
      """  [[[[[[[[[[[[[[[[[[[[[[["Too deep"]]]]]]]]]]]]]]]]]]]]]]]    """ to "Too much nesting reached. Max nesting is 22 levels",
      """  {"Missing colon" null}                                      """ to "Error At",
      """  {"Double colon":: null}                                     """ to "Error At",
      """  {"Comma instead of colon", null}                            """ to "Error At",
      """  ["Colon instead of comma": false]                           """ to "Error At",
      """  ["Bad value", truth]                                        """ to "Error At",
      """  ['single quote']                                            """ to "Error At",
      """  [\"\ttab\tcharacter\tin\tstring\t\"]                        """ to "Error At",
      """  ["tab\   character\   in\  string\  "]                      """ to "Error At",
      """  ["line${'\n'}break"]                                        """ to "Illegal Codepoint",
      """  ["line${"\\\n"}break"]                                      """ to "Invalid escape sequence",
      """  [0e]                                                        """ to "Error At",
      """  [0e+]                                                       """ to "Error At",
      """  [0e+-1]                                                     """ to "Error At",
      """  {"Comma instead if closing brace": true,                    """ to "Error At",
      """  ["mismatch"}                                                """ to "Error At",
    )
      .forEach { (json, expectedErrorMessage) ->
        val data = ArrayReadBuffer(json.encodeToByteArray())
        val exception = assertFailsWith<IllegalStateException> {
          JSONParser().parse(data)
        }
        assertNotNull(exception.message)
        assertContains(
          exception.message!!,
          expectedErrorMessage,
          message = "Invalid JSON [${json.trim()}] didn't cause correct exception message [${expectedErrorMessage}] "
        )
      }
  }

  @Test
  fun testParseMustPass() {
    //language=JSON
    listOf(
      """
          [
            "JSON Test Pattern pass1",
            {
              "object with 1 member": [
                "array with 1 element"
              ]
            },
            {},
            [],
            -42,
            true,
            false,
            null,
            {
              "integer": 1234567890,
              "real": -9876.543210,
              "e": 0.123456789e-12,
              "E": 1.234567890E+34,
              "": 23456789012E66,
              "zero": 0,
              "one": 1,
              "space": " ",
              "quote": "\"",
              "backslash": "\\",
              "controls": "\b\f\n\r\t",
              "slash": "/ & \/",
              "alpha": "abcdefghijklmnopqrstuvwyz",
              "ALPHA": "ABCDEFGHIJKLMNOPQRSTUVWYZ",
              "digit": "0123456789",
              "0123456789": "digit",
              "special": "`1~!@#$%^&*()_+-={':[,]}|;.</>?",
              "hex": "\u0123\u4567\u89AB\uCDEF\uabcd\uef4A",
              "true": true,
              "false": false,
              "null": null,
              "array": [],
              "object": {},
              "address": "50 St. James Street",
              "url": "http://www.JSON.org/",
              "comment": "// /* <!-- --",
              "# -- --> */": " ",
              " s p a c e d ": [
                1,
                2,
                3,
                4,
                5,
                6,
                7
              ],
              "compact": [
                1,
                2,
                3,
                4,
                5,
                6,
                7
              ],
              "jsontext": "{\"object with 1 member\":[\"array with 1 element\"]}",
              "quotes": "&#34; \u0022 %22 0x22 034 &#x22;",
              "\/\\\"\uCAFE\uBABE\uAB98\uFCDE\ubcda\uef4A\b\f\n\r\t`1~!@#$%^&*()_+-=[]{}|;:',./<>?": "A key can be any string"
            },
            0.5,
            98.6,
            99.44,
            1066,
            1e1,
            0.1e1,
            1e-1,
            1e00,
            2e+00,
            2e-00,
            "rosebud"
          ]
      """,
      """
        {
          "JSON Test Pattern pass3": {
            "The outermost value": "must be an object or array.",
            "In this test": "It is an object."
          }
        }
      """,
      """[[[[[[[[[[[[[[[[[[["Not too deep"]]]]]]]]]]]]]]]]]]]""",
    )
      .forEach { json ->
        val data = ArrayReadBuffer(json.encodeToByteArray())
        JSONParser().parse(data)
      }
  }
}
