package duse12 {

import java.util.Date
import akka.util.Duration

package messages {

import java.util.concurrent.atomic.AtomicInteger

object MsgUtils {
  private val counter = new AtomicInteger()

  def newDate = new Date(System.currentTimeMillis())

  def genNextId = counter.incrementAndGet()

}

import MsgUtils._

trait JunctionEvent {

}

/**
 * message sent to the sensor that a vehicle is detected,
 * crossedMarker indicates if the vehicle crossed the marker on the road (passed the sensor)
 * or if it is approaching the junction
 */
case class VehicleDetected(id: Int, crossedMarker: Boolean = false, timestamp: Date = newDate) extends JunctionEvent

/**
 * message sent from sensor to junction, that a vehicle is queueCount on the lane
 * and that should be sent to the TrafficLight (for display purposes).
 */
case class VehicleQueued(id: Int, lane: LANE.HEADING, queueCount: Int, timestamp: Date = newDate) extends JunctionEvent

case class CountVehiclesRequest()

/**
 * message sent from the sensor to the junction that a vehicle has passed the traffic light
 */
case class VehiclePassed(id: Int, lane: LANE.HEADING, queueCount: Int, timestamp: Date = newDate) extends JunctionEvent

/**
 * Command to switch a traffic light on a lane to green.
 * If a TrafficLight gets a GreenLight signal for another lane, it should switch to red.
 */
case class GreenLight(lane: LANE.HEADING)

/**
 * Command to control traffic, which should be sent by a scheduler to the junction.
 */
case class ControlTraffic()

/**
 * Resets the Junction
 */
case class ResetJunction() extends JunctionEvent

/**
 * The decision the junction has made at a specific time, result of ControlTraffic command.
 */
case class JunctionDecision(lane: LANE.HEADING) extends JunctionEvent


case class DecisionsRequest()

case class DecisionsResponse(history: List[JunctionEvent])


}
}