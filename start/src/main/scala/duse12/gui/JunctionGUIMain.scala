package duse12.gui

import scala.swing._
import duse12.LANE

/**
* Main method that launches the Junction GUI
*/
object JunctionGUIMain extends SimpleSwingApplication {
  def top =
    new MainFrame {
      title = "Scala Junction App"
      val junction = JunctionGUIAssembler.assemble
      contents = junction
    }
}

/**
* GUI assembler
*/
object JunctionGUIAssembler {

  def assemble() = {
      val northLane = LANE.NORTH
      val eastLane = LANE.EAST
      val westLane = LANE.WEST

      val listener = new DummySensorListener
      val northSens = new Sensor(northLane, listener)
      val eastSens = new Sensor(eastLane, listener)
      val westSens = new Sensor(westLane, listener)

      val north = TrafficLightWidget(northLane, 570, 280, northSens)
      var northSensBtn = new SensorButton(northLane, 375, 530, northSens)
      WidgetRegistry.registry += (northLane -> (north, northSensBtn))

      val east = TrafficLightWidget(eastLane, 120, 280, eastSens)
      var eastSensBtn = new SensorButton(eastLane, 20, 105, eastSens)
      WidgetRegistry.registry += (eastLane -> (east, eastSensBtn))

      var west = TrafficLightWidget(westLane, 570, 40, westSens)
      var westSensBtn = new SensorButton(westLane, 840, 105, westSens)
      WidgetRegistry.registry += (westLane -> (west, westSensBtn))

      val sensorRandomizer = new SensorRandomizerButton(0, 0, List(northSens, eastSens, westSens))
      new ImagePanel("/crossing.png", sensorRandomizer, north, east, west, northSensBtn, eastSensBtn, westSensBtn)
  }
}