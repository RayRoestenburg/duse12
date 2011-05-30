package duse12
import duse12.messages._
import akka.actor.{ActorRef, Actor}
import collection.mutable.HashMap
import akka.event.EventHandler

/**
 * The junction of roads, receives vehicles and communicates with TrafficLights
 */
class Junction(trafficLights: List[ActorRef], listener: ActorRef) extends Actor {
  val map = new HashMap[LANE.HEADING, Int]
  var lastDecision = LANE.WEST

  def receive = {
    case msg: VehicleQueued => {
      // process queued vehicles,
      EventHandler.info(this, "Vehicle queued on %s lane.".format(msg.lane) )
      map.get(msg.lane) match {
        case Some(count) => map.put(msg.lane, count + 1)
        case None => map.put(msg.lane, 1)
      }
      listener.forward(msg)
    }
    case msg: VehiclePassed => {
      EventHandler.info(this, "Vehicle passed on %s lane.".format(msg.lane) )
      // process passed vehicles,
      map.get(msg.lane) match {
        case Some(count) => map.put(msg.lane, count - 1)
        case None => map.put(msg.lane, 0)
      }
      listener.forward(msg)
    }
    case msg: ControlTraffic => {
      // decide on green light,
      EventHandler.info(this, "Deciding which lane gets the green signal.")
      var greenLane = lastDecision
      if (map.isEmpty) {
        lastDecision = LANE.nextClockwise(lastDecision)
        listener ! JunctionDecision(lastDecision)
        greenLane = lastDecision
      } else {
        // let the most queued vehicles through
        val order = Ordering[Int].on[(LANE.HEADING, Int)](_._2)
        val greenLane = map.max(order)._1
        listener ! JunctionDecision(greenLane)
      }
      EventHandler.info(this, "Green signal for %s lane." format greenLane)
      // just broadcast to all traffic lights.
      trafficLights.foreach(_ ! GreenLight(greenLane))
      //TODO decrement the lane with a fixed size here
    }
    case msg: ResetJunction => {
      //reset the junction.
      EventHandler.info(this, "Resetting junction.")
      map.clear
      lastDecision = LANE.WEST
      listener.forward(msg)
    }
    case msg@_ => {
      EventHandler.error(this, "Unknown msg '%s'received in Junction." format msg)
    }
  }
}