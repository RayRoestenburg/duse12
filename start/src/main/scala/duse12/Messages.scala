package duse12 {

import java.util.Date
import akka.util.Duration

package messages {

  object MsgUtils {
    def newDate = new Date(System.currentTimeMillis())
  }
  import MsgUtils._
  /**
   * message sent to the sensor that a vehicle is detected,
   * crossedMarker indicates if the vehicle crossed the marker on the road (passed the sensor)
   * or if it is approaching the junction
   */
  case class VehicleDetected(id: Int, crossedMarker: Boolean = false, timestamp: Date = newDate)

  /**
   * message sent from sensor to junction, that a vehicle is queued on the lane
   * and that should be sent to the TrafficLight (for display purposes).
   */
  case class VehicleQueued(id: Int, lane: LANE.HEADING, timestamp: Date = newDate)

  /**
   * message sent from the sensor to the junction that a vehicle has passed the traffic light
   */
  case class VehiclePassed(id: Int, lane: LANE.HEADING, timestamp: Date = newDate)

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
  case class ResetJunction()

  /**
   * The decision the junction has made at a specific time, result of ControlTraffic command.
   */
  case class JunctionDecision(lane: LANE.HEADING)

/* TODO implement these in queries and commands
  /**
   * Query a traffic light
   */
  case class QueryTrafficLight(lane: LANE.HEADING)

  /**
   * Query all traffic lights
   */
  case class QueryAllTrafficLights()

  /**
   * Query all sensors
   */
  case class QueryAllSensors()

  /**
   * Query a sensor on a lane
   */
  case class QuerySensor(lane: LANE.HEADING)

  /**
   * Query the junction
   */
  case class QueryJunction()

  /**
   * View of how long a vehicle has waited at the junction
   */
  case class VehicleData(id: Int, lane: LANE.HEADING, waitTime: Duration)

  /**
   * View of how long the TrafficLight has been on green, orange and red
   */
  case class TrafficLightData(lane: LANE.HEADING, timeGreen: Duration, timeOrange: Duration, timeRed: Duration, timeOff: Duration)

  /**
   * View of how many vehicles were detected by the sensor
   */
  case class SensorData(lane: LANE.HEADING, countDetected: Int)
*/
}
}