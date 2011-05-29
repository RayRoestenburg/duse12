package duse12.gui

import duse12.LANE._
import duse12.gui._
import java.util.concurrent.atomic.AtomicInteger

//=============================================
// Controllers
//=============================================

/**
 * SensorListener that listens to
 * increment and decrement events
 */
trait SensorListener {
  def onIncrement(sensor: Sensor): Unit = Unit
  def onDecrement(sensor: Sensor): Unit = Unit
}
/**
 * Sensor that counts the amount
 * of incrementing and decrementing impulses
 * TODO This class needs to be converted to an actor
 */
class Sensor(val lane:HEADING, listeners:SensorListener*) {
  val queued = new AtomicInteger()
  def increment =  {
    val r = queued.incrementAndGet()
    listeners.foreach(_.onIncrement(this))
    r
  }
  def decrement = {
    val r = queued.decrementAndGet()
    listeners.foreach(_.onDecrement(this))
    r
  }
  def current = queued.get()
}

//=============================================
// Dummy Controller impls
//=============================================

class DummySensorListener extends SensorListener {
  /**
   * Dummy listener that simulates some behaviour.
   * For the final implementation this behaviour is not wanted.
   * It is not desirable for the sensor to change the state
   * of his corresponding trafficlight.
   *
   * @param sensor sensor
   */

  private def getWidgetsForLane(lane:HEADING):Option[(TrafficLightWidget, SensorButton)] =  WidgetRegistry.registry.get(lane)

  override def onIncrement(sensor: Sensor): Unit = {
    val(trafficLightWidget, sensorBtn) =  getWidgetsForLane(sensor.lane).get
    sensorBtn.refresh
    if (sensor.current % 5 == 0) {
      trafficLightWidget.switchToRed
    }
    else if (sensor.current  % 7 == 0) {
      trafficLightWidget.switchToGreen
    }
  }

  override def onDecrement(sensor: Sensor): Unit = {
      getWidgetsForLane(sensor.lane).get._2.refresh
  }
}
