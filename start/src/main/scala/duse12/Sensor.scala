package duse12

import akka.actor.{ ActorRef, Actor }
import akka.event.EventHandler
import duse12.messages._
/**
 * Sensor Actor, handles VehicleDetected for a lane.
 */
class Sensor(lane: LANE.HEADING, junction: ActorRef) extends Actor {
  var queueCount = 0

  def receive = {
    case msg: VehicleDetected if !msg.crossedMarker => {
      EventHandler.info(this,"Vehicle approaching  %s lane sensor.".format(lane) )
      queueCount += 1
      junction ! VehicleQueued(msg.id, lane, queueCount, msg.timestamp)
      self.reply(queueCount)
    }
    case msg: VehicleDetected if msg.crossedMarker => {
      EventHandler.info(this,"Vehicle passed %s lane sensor.".format(lane) )
      queueCount -= 1
      junction ! VehiclePassed(msg.id, lane, queueCount, msg.timestamp)
      self.reply(queueCount)
    }
    case msg@_ => {
      EventHandler.error(this, "Unknown msg '%s'received in Sensor." format msg)
    }
  }
}
