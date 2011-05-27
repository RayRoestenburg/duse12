package duse12

import akka.actor.{ ActorRef, Actor }
import collection.mutable.HashMap
import org.apache.camel.component.language.LanguageEndpoint
import java.util.concurrent.CountDownLatch
import duse12.JunctionDecision
import java.util.Date


/**
 * The junction of roads, receives vehicles and communicates with TrafficLights
 */
class Junction(trafficLights: List[ActorRef], listener:ActorRef) extends Actor {
  val map = new HashMap[Lane.Side, Int]

  def receive = {
    case msg: VehicleQueued => {
      // process queued vehicles,
      map.get(msg.lane) match {
        case Some(count) => map.put(msg.lane,count+1)
        case None => map.put(msg.lane, 1)
      }
      listener.forward(msg)
    }
    case msg: ControlTraffic=> {
      // let the most queued vehicles through
      val greenLane = map.keys.toList.sortWith((one, other) => one > other).head
      listener ! JunctionDecision(greenLane)
    }
    case msg: ResetJunction=> {
      // process reset
      listener.forward(msg)
    }
    // add a msg to handle the timer
    case _ => {
    }
  }
}