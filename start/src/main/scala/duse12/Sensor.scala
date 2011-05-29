package duse12

import akka.actor.{ ActorRef, Actor }
/**
 *
 */
class Sensor(lane: LANE.HEADING, junction: ActorRef) extends Actor {
  def receive = {
    case msg: VehicleDetected => {
      // remote actor receives VehicleDetected
      println("vehicle detected!")
      junction ! msg
    }
  }
}