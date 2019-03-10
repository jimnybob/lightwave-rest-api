package uk.co.smartii.akka

import akka.actor.FSM
import play.api.libs.json.Json
import uk.co.smartii.akka.TimerSwitchFSM._

object TimerSwitchFSM {

  sealed trait State

  case object Idle extends State

  sealed trait Data

  case object On extends Data

  case object Off extends Data

  case class TestMessage(msg: String)

  case class TestResponse(resp: String)

  object TestResponse {
    implicit val format = Json.format[TestResponse]
  }

}

class TimerSwitchFSM extends FSM[State, Data] {

  startWith(Idle, Off)

  when(Idle) {
    case Event(TestMessage(msg), _) =>
      sender ! TestResponse(s"received $msg")
      stay
  }
}
