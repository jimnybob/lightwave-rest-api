package uk.co.smartii.lightwave.model

import com.lightwaverf.api.model.DeviceMessage

/**
  * Created by jimbo on 14/12/16.
  */
sealed trait Event

case class Dim(percent: Int) extends Event

sealed trait OnOffEvent extends Event

object OnOffEvent {

  def getEvent(deviceMessage: DeviceMessage): OnOffEvent = deviceMessage.fn match {
    case "on" => On
    case "off" => Off
    case other => throw new MatchError(s"Unable to match on/off event for value $other")
  }
}

case object On extends OnOffEvent

case object Off extends OnOffEvent

