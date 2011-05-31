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
        case Some(count) => map.put(msg.lane, msg.queueCount)
        case None => map.put(msg.lane, msg.queueCount)
      }
      listener.forward(msg)
    }
    case msg: VehiclePassed => {
      EventHandler.info(this, "Vehicle passed on %s lane.".format(msg.lane) )
      // process passed vehicles,
      map.get(msg.lane) match {
        case Some(count) => map.put(msg.lane, msg.queueCount)
        case None => map.put(msg.lane, msg.queueCount)
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
        EventHandler.info(this, "North:"+map.getOrElse(LANE.NORTH,0))
        EventHandler.info(this, "West:"+map.getOrElse(LANE.WEST,0))
        EventHandler.info(this, "East:"+map.getOrElse(LANE.EAST,0))
        // let the most queued vehicles through
        greenLane = map.maxBy( _._2 )._1
        listener ! JunctionDecision(greenLane)
      }
      EventHandler.info(this, "Green signal for %s lane." format greenLane)
      // just broadcast to all traffic lights.
      trafficLights.foreach(_ ! GreenLight(greenLane))
    }
    case msg: ResetJunction => {
      //reset the junction.
      EventHandler.info(this, "Resetting junction.")
      map.clear
      lastDecision = LANE.WEST
      listener.forward(msg)
    }
    case msg@_ => {
      EventHandler.error(this, "Unknown msg '%s' received in Junction." format msg)
    }
  }

}