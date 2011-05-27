package duse12

import java.util.Date
import akka.actor.{ ActorRef, Actor }

/**
 *
 */
class Sensor(lane: Int, junction: ActorRef) extends Actor {
  def receive = {
    case msg: VehicleDetected => {
      // remote actor receives VehicleDetected
      println("vehicle detected!")
      junction ! msg
    }
  }
}
case class VehicleDetected(timestamp: Date)
case class VehicleQueued(lane: Int, timestamp: Date)