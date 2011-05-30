package duse12

import duse12.messages._
import akka.actor.{ActorRef, Actor}

/**
 * Handle forwarded commands and queries
 */
class JunctionQueries(listener:Option[ActorRef]=None) extends Actor {
  private var events = List[JunctionEvent]()

  def receive = {
    case msg: VehiclePassed => {
      events = msg::events
      listener.foreach(_ ! msg)
    }
    case msg: VehicleQueued => {
      events = msg::events
      listener.foreach(_ ! msg)
    }
    case msg: ResetJunction => {
      events = msg::events
      listener.foreach(_ ! msg)
    }
    case msg: JunctionDecision => {
      events = msg::events
      listener.foreach(_ ! msg)
    }
    case msg: DecisionsRequest => {
      var history = List[JunctionDecision]()
      for(event <-events){
        event match {
          case msg: JunctionDecision =>
            history = msg :: history
        }
      }
      self.reply(DecisionsResponse(history))
    }
  }
}