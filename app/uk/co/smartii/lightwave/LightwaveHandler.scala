package uk.co.smartii.lightwave

import java.net._
import java.nio.channels.DatagramChannel

import akka.actor.{ActorRef, LoggingFSM, Stash}
import akka.io.Inet.{DatagramChannelCreator, SocketOptionV2}
import akka.io.Udp.Send
import akka.io.{IO, Udp, UdpConnected}
import akka.util.ByteString
import com.lightwaverf.api.model.DeviceMessage
import play.api.libs.json.Json
import uk.co.smartii.akka.HouseCoordinator.LightwavePayload
import uk.co.smartii.lightwave.LightwaveHandler._
import uk.co.smartii.lightwave.model.{DimmerDevEvent, OnOffDevEvent, isDimEvent, isOnOffEvent}

object LightwaveHandler {

  sealed trait State

  case object Binding extends State

  case object Registering extends State

  case object Connected extends State

  sealed trait Data

  case object Empty extends Data

  case class Bound(connection: ActorRef) extends Data

}

class LightwaveHandler extends LoggingFSM[State, Data] with Stash {

  import context.system

  log.debug("Starting LightwaveHandler")
  startWith(Binding, Empty)

  private val ip = "255.255.255.255"
  private val broadcastPort = new InetSocketAddress("192.168.x.x", 9760)

  private val transmitSocket = new DatagramSocket()

  // 9761 is the receive port
  private val LightwaveUdpBroadcastPort = 9761
  private val remote = new InetSocketAddress(LightwaveUdpBroadcastPort)

  private val exampleCommand = "!R3D1F1"

  private val lookupService: Lookup = new Lookup {
    override def getRoomName(id: Int) = "kitchen"

    override def getDeviceName(id: Int) = "device"
  }

  IO(Udp) ! Udp.Bind(self, remote)

  when(Binding) {
    case Event(Udp.Bound(local), _) =>
      log.debug("Successfully bound")
      goto(LightwaveHandler.Registering) using Bound(sender())
  }

  when(Registering) {
    case Event(Udp.Received(data, remote), _) if (data.utf8String.contains("success")) =>
      log.debug("Successfully registered")
      goto(LightwaveHandler.Connected)
    case Event(Udp.Received(data, remote), _) =>
      log.debug("Failed to register: " + data.utf8String)
      stay
    case Event(LightwavePayload(command), Bound(connection)) =>
      log.debug("Sending: " + command)
      connection ! Udp.Send(ByteString(command), remote)
      stay
  }

  when(Connected) {
    case Event(LightwavePayload(command), Bound(connection)) =>
      log.debug("Sending: " + command)
      connection ! Udp.Send(ByteString(command), remote)
      stay
    case Event(Udp.Received(data, remote), _) =>
      log.debug("Received: " + data.utf8String)
      Json.fromJson[DeviceMessage](Json.parse(data.utf8String)) match {
        case msg@isDimEvent() => {
          DimmerDevEvent(msg).run(lookupService)
        }
        case msg@isOnOffEvent() => {
          OnOffDevEvent(msg).run(lookupService)
        }
        case unknownMsg => log.error(new IllegalArgumentException(s"Unable to deserialise message: $unknownMsg"), s"Unable to deserialise message: $unknownMsg")
      }
      stay
    case Event(Udp.Unbind, Bound(socket)) =>
      socket ! Udp.Unbind
      stay
    case Event(Udp.Unbound, _) =>
      context.stop(self)
      stay
  }

  onTransition {
    case Binding -> Registering =>
//      val opts = List(InetProtocolFamily(), MulticastGroup(ip, "enp0s31f6"))
//      IO(Udp) ! Udp.Bind(self, new InetSocketAddress(9760), opts)
//        IO(Udp) ! Udp.Send(ByteString("!F*p"), broadcastPort)
      val sendData = "!F*p".getBytes
      val address = InetAddress.getByName(ip)
      val sendPacket = new DatagramPacket(sendData, sendData.length, address, 9760) //Send broadcast UDP to 9760 port

      transmitSocket.send(sendPacket)
//      IO(UdpConnected) ! UdpConnected.Connect(self, broadcastPort)
//      nextStateData.asInstanceOf[Bound].connection ! Udp.Send(ByteString("!F*p"), broadcastPort)
  }
}

final case class InetProtocolFamily() extends DatagramChannelCreator {
  override def create() =
    DatagramChannel.open(StandardProtocolFamily.INET)
}

final case class MulticastGroup(address: String, interface: String) extends SocketOptionV2 {
  override def afterBind(s: DatagramSocket): Unit = {
    val group = InetAddress.getByName(address)
    val networkInterface = NetworkInterface.getByName(interface)
    s.getChannel.join(group, networkInterface)
  }
}