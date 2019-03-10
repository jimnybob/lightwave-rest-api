package uk.co.smartii.lightwave

/**
  * Created by jimbo on 17/12/16.
  */
trait Lookup {
  def getDeviceName(id: Int): String
  def getRoomName(id: Int): String
}
