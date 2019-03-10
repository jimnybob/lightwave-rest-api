package com.lightwaverf.api.model

import play.api.libs.json._
import scala.util.Try

/**
  * Created by jimbo on 15/12/16.
  */
sealed trait LightwaveMessage {
  def trans: Int
  def mac: String
  def time: Long
  def pkt: String
}


object DeviceMessage {

  implicit val format = Json.format[DeviceMessage]

//  def apply2: PartialFunction[String, DeviceMessage] = {
//    case message: String if message.startsWith("*!") && Try(message.drop(2).parseJson.convertTo[DeviceMessage](DeviceMessageJson.format)).isSuccess =>
//      Json.format[DeviceMessage](Json.parse(message.drop(2)))
//      message.drop(2).parseJson.convertTo[DeviceMessage](DeviceMessageJson.format)
//    case otherMsg => throw new MatchError(s"Lightwave messages should start with '*!'. Message is: $otherMsg")
//  }
//
//  def apply(message: String): DeviceMessage = message match {
//    case message: String if message.startsWith("*!") && Try(message.drop(2).parseJson.convertTo[DeviceMessage](DeviceMessageJson.format)).isSuccess => message.drop(2).parseJson.convertTo[DeviceMessage](DeviceMessageJson.format)
//    case otherMsg => throw new MatchError(s"Lightwave messages should start with '*!'.  Message is: $otherMsg")
//  }
}

case class DeviceMessage(trans: Int, mac: String, time: Long, pkt: String, fn: String, room: Int, dev: Int, param: Option[Int]) extends LightwaveMessage
