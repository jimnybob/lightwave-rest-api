package uk.co.smartii.lightwave.model

import cats.Id
import cats.data.{Kleisli, Reader}
import com.lightwaverf.api.model.DeviceMessage
import uk.co.smartii.lightwave.Lookup

/**
  * Created by jimbo on 14/12/16.
  */
sealed trait Device

object OnOff {

  def apply(deviceMessage: DeviceMessage): Reader[Lookup, Kleisli[Id, Lookup, OnOff]] = Reader { lookup: Lookup =>
    Room(deviceMessage).map { room =>
      OnOff(id = deviceMessage.dev, name = lookup.getDeviceName(deviceMessage.dev), room = room)
    }
  }
}

case class OnOff(id: Int, name: String, room: Room) extends Device

object Dimmer {

  def apply(deviceMessage: DeviceMessage): Reader[Lookup, Kleisli[Id, Lookup, Dimmer]] = Reader { lookup: Lookup =>
    Room(deviceMessage).map { room =>
      Dimmer(id = deviceMessage.dev, name = lookup.getDeviceName(deviceMessage.dev), room = room)
    }
  }

  def apply2(deviceMessage: DeviceMessage): Reader[Lookup, Kleisli[Id, Lookup, Dimmer]] = Reader { lookup: Lookup =>
    Room(deviceMessage).map { room =>
      Dimmer(id = deviceMessage.dev, name = lookup.getDeviceName(deviceMessage.dev), room = room)
    }
  }
}

case class Dimmer(id: Int, name: String, room: Room) extends Device
