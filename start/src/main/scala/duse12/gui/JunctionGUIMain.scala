package duse12.gui

import scala.swing._
import akka.actor.Actor._
import duse12.{TrafficLight, LANE}

/**
 * Main method that launches the Junction GUI
 */
object JunctionGUIMain extends SimpleSwingApplication {
  def top =
    new MainFrame {
      title = "Scala Junction App"
      val junction = JunctionGUIAssembler.assemble
      val west = WidgetRegistry.registry.get(LANE.WEST).get._1
      val north = WidgetRegistry.registry.get(LANE.NORTH).get._1
      val east = WidgetRegistry.registry.get(LANE.EAST).get._1

      val westActor = actorOf(new TrafficLight(LANE.WEST,west))
      val northActor = actorOf(new TrafficLight(LANE.NORTH,north))
      val eastActor =actorOf(new TrafficLight(LANE.EAST,east))
      remote.start("localhost",2553)
      remote.register("trafficLight-West",westActor.start)
      remote.register("trafficLight-North",northActor.start)
      remote.register("trafficLight-East",eastActor.start)

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


    val westSens = remote.actorFor("sensor-West", "localhost", 2552)
    val eastSens = remote.actorFor("sensor-East", "localhost", 2552)
    val northSens = remote.actorFor("sensor-North", "localhost", 2552)
    //val south = remote.actorFor("trafficLight-Bottom", "localhost", 2553)
    val north = TrafficLightWidget(northLane, 570, 280, northSens)
    var northSensBtn = new SensorButton(northLane, 375, 530, northSens)
    WidgetRegistry.registry += (northLane -> (north, northSensBtn))

    val east = TrafficLightWidget(eastLane, 120, 280, eastSens)
    var eastSensBtn = new SensorButton(eastLane, 20, 105, eastSens)
    WidgetRegistry.registry += (eastLane -> (east, eastSensBtn))

    var west = TrafficLightWidget(westLane, 570, 40, westSens)
    var westSensBtn = new SensorButton(westLane, 840, 105, westSens)
    WidgetRegistry.registry += (westLane -> (west, westSensBtn))

    //TODO where is south? (also, the png is representing north as 'down')

    val sensorRandomizer = new SensorRandomizerButton(0, 0, List(northSens, eastSens, westSens))
    new ImagePanel("/crossing.png", sensorRandomizer, north, east, west, northSensBtn, eastSensBtn, westSensBtn)
  }
}