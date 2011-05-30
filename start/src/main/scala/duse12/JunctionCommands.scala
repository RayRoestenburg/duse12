package duse12

import akka.actor.{ActorRef, Actor}
import collection.mutable.HashMap
import collection.immutable.List._
import duse12.messages._

/**
 * JunctionCommands, forwards commands to Queries and handles the SnapshotRequest command,
 * which creates a List of all Decisions made.
 */
class JunctionCommands(queries: ActorRef) extends Actor {

  def receive = {
    case msg: VehiclePassed => {
      queries.forward(msg)
    }
    case msg: VehicleQueued => {
      queries.forward(msg)
    }
    case msg: ResetJunction => {
      queries.forward(msg)
    }
    case msg: JunctionDecision => {
      queries.forward(msg)
    }
  }
}