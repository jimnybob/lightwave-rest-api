package uk.co.smartii.lightwave.model

import cats.data.Reader
import com.lightwaverf.api.model.DeviceMessage
import uk.co.smartii.lightwave.Lookup


/**
  * Created by jimbo on 14/12/16.
  */
object Room {

  def apply(deviceMessage: DeviceMessage): Reader[Lookup, Room] = Reader[Lookup, Room] { roomLookup =>
    Room(id = deviceMessage.room, name = roomLookup.getRoomName(deviceMessage.room))
  }
}

case class Room(id: Int, name: String)
