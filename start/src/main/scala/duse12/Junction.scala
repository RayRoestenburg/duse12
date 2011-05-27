package duse12

import akka.actor.Actor
import akka.actor.Actor._
import java.util.Timer

/**
 *
 */
class Junction extends Actor {
  def receive = {
    case msg: VehicleQueued => {
      // process queued vehicles,
    }
    // add a msg to handle the timer
    case _ => {
    }
  }
}