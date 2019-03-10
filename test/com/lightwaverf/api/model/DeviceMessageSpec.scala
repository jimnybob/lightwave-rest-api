package com.lightwaverf.api.model

import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json.Json

class DeviceMessageSpec extends FlatSpec with Matchers {

  private val json =
    """*!{
      |    "trans":1,
      |    "mac":"03:45:67",
      |    "time":1420070400,
      |    "pkt":"433T",
      |    "fn":"dim",
      |    "room":1,
      |    "dev":1,
      |    "param":16
      |}""".stripMargin

  private val deviceMessage = DeviceMessage(trans = 1, mac = "03:45:67", time = 1420070400, pkt = "433T", fn = "dim", room = 1, dev = 1, param = Some(16))

  "The DeviceMessage JSON formatter" should "be able to read Json" in {
    Json.fromJson[DeviceMessage](Json.parse(json.drop(2))).get shouldBe deviceMessage
  }

  it should "be able to write Json" in {

    Json.toJson(deviceMessage) shouldBe Json.parse(json.drop(2))
  }
}
