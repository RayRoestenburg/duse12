package duse12

import java.util.Date
import akka.util.Duration

/**
 * message sent to the sensor that a vehicle is detected
 * and that should be sent to the TrafficLight (for display purposes).
 */
case class VehicleDetected(id:Int, timestamp: Date)

/**
 * message sent from sensor to junction, that a vehicle is queued on the lane
 * and that should be sent to the TrafficLight (for display purposes).
 */
case class VehicleQueued(id:Int, lane: Lane.Side, timestamp: Date)

/**
 * message that needs to be stored that a vehicle has passed the traffic light at the lane
 * and that should be sent to the TrafficLight (for display purposes).
 */
case class VehiclePassed(id:Int, lane:Lane.Side, timestamp: Date)

/**
 * TODO rename, en boolean
 * Command to switch a TrafficLight to a color. The TrafficLight Remote Actor runs in the swing app.
 */
case class ChangeTrafficLightColor(color: Signal.Color)

/**
 * Command to switch a traffic light on or off
 */
case class SwitchTrafficLight(on:Boolean)

/**
 * enumeration for the signal light  (orange is handled in the ui)
 */
object Signal extends Enumeration {
  type Color = Value
  val Red = Value("Red")
  val Green = Value("Green")
  val Orange = Value("Orange")
//TODO off hoeft niet
  val Off= Value("Blinking Orange")
}
///TODO north etc
object Lane extends Enumeration {
  type Side = Value
  val Left = Value(1)
  val Right = Value(3)
  val Top = Value(2)
  val Bottom= Value(4)
}
/**
 * Command to control traffic, which should be sent by a scheduler to the junction.
 */
case class ControlTraffic()

/**
 * Resets the Junction
 */
case class ResetJunction()

/**
 * Query a traffic light
 */
case class QueryTrafficLight(lane:Lane.Side)

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
case class QuerySensor(lane: Lane.Side)

/**
 * Query the junction
 */
case class QueryJunction()

/**
 * View of how long a vehicle has waited at the junction
 */
case class VehicleData(id:Int, lane:Lane.Side, waitTime:Duration)

/**
 * View of how long the TrafficLight has been on green, orange and red
 */
case class TrafficLightData(lane:Lane.Side, timeGreen:Duration,timeOrange:Duration, timeRed:Duration, timeOff:Duration)

/**
 * View of how many vehicles were detected by the sensor
 */
case class SensorData(lane:Lane.Side, countDetected:Int)

/**
 * The decision the junction has made at a specific time, result of ControlTraffic command.
 */
case class JunctionDecision(lane:Lane.Side)