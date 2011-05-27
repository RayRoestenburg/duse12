package duse12

import akka.actor.{ ActorRef, Actor }

/**
 * The junction of roads, receives vehicles and communicates with TrafficLights
 */
class Junction(trafficLights: List[ActorRef]) extends Actor {
  def receive = {
    case msg: VehicleQueued => {
      // process queued vehicles,
    }
    // add a msg to handle the timer
    case _ => {
    }
  }
}