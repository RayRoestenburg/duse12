package duse12

import akka.actor.Actor

/**
 * A TrafficLight, receives switch messages
 */
class TrafficLight(lane:LANE.HEADING) extends Actor {
  def receive = {
    case _ => {

    }
  }
}