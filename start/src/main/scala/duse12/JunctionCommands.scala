package duse12

import akka.actor.{ActorRef, Actor}
import duse12.messages._

/**
 * JunctionCommands, forwards commands to Queries
 */
class JunctionCommands(queries: ActorRef) extends Actor {

  def receive = {
    case msg: JunctionEvent=> {
      queries.forward(msg)
      //TODO do some recording, and handle commands.
    }
  }
}