package duse12

import akka.actor.Actor
import akka.event.EventHandler
import duse12.messages._
import scala.Some

/**
 * A TrafficLight, receives GreenLight messages, turns the light to red or green.
 */
class TrafficLight(lane: LANE.HEADING, lightSwitch: LightSwitch) extends Actor {
  // false is red
  var maybeGreen: Option[Boolean] = None

  def receive = {
    case msg: GreenLight => {
      def switch(switchToGreen: Boolean, currentState: Boolean): Boolean = {
        if (switchToGreen != currentState) {
          EventHandler.info(this, "TrafficLight on '%s' lane is switched to red" format (lane))
          if (switchToGreen) {
            lightSwitch.switchToGreen
          } else {
            lightSwitch.switchToRed
          }
          !currentState
        } else {
          currentState
        }
      }
      maybeGreen = maybeGreen.map {
        currentState => switch(lane == msg.lane, currentState)
      }.orElse {
        lightSwitch.switchToRed
        Some(false)
      }
    }
    case msg@_ => {
      EventHandler.error(this, "Unknown msg '%s' received in TrafficLight." format msg)
    }
  }
}