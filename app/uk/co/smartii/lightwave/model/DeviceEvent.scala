package uk.co.smartii.lightwave.model

import cats.data.{Kleisli, Reader}
import com.lightwaverf.api.model.DeviceMessage
import uk.co.smartii.lightwave.Lookup

/**
  * Created by jimbo on 14/12/16.
  */
sealed trait DeviceEvent {
  def device: Device
  def event: Event
}

case class OnOffDevEvent(device: OnOff, event: OnOffEvent)

object OnOffDevEvent {

  def apply(deviceMessage: DeviceMessage):  Reader[Lookup, OnOffDevEvent] = deviceMessage match {
    case deviceMessage @ isOnOffEvent() => {
      OnOff(deviceMessage).flatMap[OnOffDevEvent](k => k.map[OnOffDevEvent](o => OnOffDevEvent(o, OnOffEvent.getEvent(deviceMessage))))
    }
  }
}

object DimmerDevEvent {

  def apply(deviceMessage: DeviceMessage):  Reader[Lookup, DimmerDevEvent] = deviceMessage match {
    case deviceMessage @ isDimEvent() => {
      Dimmer(deviceMessage).flatMap[DimmerDevEvent](k => k.map[DimmerDevEvent](d => DimmerDevEvent(d, Dim(deviceMessage.param.get))))
    }
  }
}

object isOnOffEvent {

  def unapply(deviceMessage: DeviceMessage): Boolean = deviceMessage.fn == "on" || deviceMessage.fn == "off"
}

object isDimEvent {

  def unapply(deviceMessage: DeviceMessage): Boolean = deviceMessage.fn == "dim"
}

case class DimmerDevEvent(device: Dimmer, event: Dim) extends DeviceEvent