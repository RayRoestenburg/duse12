package duse12

import akka.actor.Actor
import akka.event.EventHandler
import duse12.messages._
/**
 * A TrafficLight, receives GreenLight messages, turns the light to red or green.
 */
class TrafficLight(lane:LANE.HEADING, lightSwitch:LightSwitch) extends Actor {
  def receive = {
    case msg:GreenLight => {
      if(msg.lane ==lane){
        EventHandler.info(this, "TrafficLight on %s lane is switched to green" format(lane))
        lightSwitch.switchToGreen
      } else {
        EventHandler.info(this, "TrafficLight on %s lane is switched to red" format(lane))
        lightSwitch.switchToRed
      }
    }
    case msg@_ => {
      EventHandler.error(this, "Unknown msg '%s'received in Sensor." format msg)
    }
  }
}