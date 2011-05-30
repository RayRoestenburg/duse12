package duse12

import akka.actor.Actor._

/**
 * Boot class
 */
class Boot {
  val west = remote.actorFor("trafficLight-West", "localhost", 2553)
  val east = remote.actorFor("trafficLight-East", "localhost", 2553)
  val north = remote.actorFor("trafficLight-North", "localhost", 2553)

  val commands = actorOf(new JunctionCommands()).start
  val junction = actorOf(new Junction(trafficLights = List(north,east, west),listener = commands)).start
  val westSensor = actorOf(new Sensor(LANE.WEST,junction)).start
  val eastSensor = actorOf(new Sensor(LANE.EAST,junction)).start
  val northSensor = actorOf(new Sensor(LANE.NORTH,junction)).start
  remote.register("sensor-West",westSensor)
  remote.register("sensor-East",eastSensor)
  remote.register("sensor-North",northSensor)
  remote.register("junction", junction)
  remote.start("localhost", 2552)
  //TODO register JunctionCommands and Queries as remote actors and call these from swing app.
  //
}
