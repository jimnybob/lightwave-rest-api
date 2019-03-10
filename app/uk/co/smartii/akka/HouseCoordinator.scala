package uk.co.smartii.akka

import akka.actor.{Actor, ActorRef, Props}
import uk.co.smartii.akka.HouseCoordinator.{DeviceMessage, LightwavePayload}
import uk.co.smartii.akka.TimerSwitchFSM.TestResponse
import uk.co.smartii.lightwave.LightwaveHandler

import scala.collection.mutable

object HouseCoordinator {
  def props = Props[HouseCoordinator]

  case class DeviceMessage[T](deviceId: String, msg: T)
  case class LightwavePayload(command: String)
}

class HouseCoordinator extends Actor {

  private var devices: mutable.Map[String, ActorRef] = mutable.Map.empty
  private val lightwaveHandler = context.actorOf(Props(classOf[LightwaveHandler]))

  override def receive: Receive = {
    case DeviceMessage(id, payload: LightwavePayload) =>
      lightwaveHandler ! payload
      sender ! TestResponse("ok")
    case DeviceMessage(id, msg) =>
      val switch = devices.getOrElseUpdate(id, context.actorOf(Props(classOf[TimerSwitchFSM])))

      switch forward msg
  }
}
