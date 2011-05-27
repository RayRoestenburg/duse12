package duse12

import akka.actor.Actor

/**
 * A TrafficLight, receives switch messages
 */
class TrafficLight(lane:Lane.Side) extends Actor {
  def receive = {
    case _ => {

    }
  }
}