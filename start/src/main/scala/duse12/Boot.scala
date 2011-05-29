package duse12

import akka.actor.Actor
import akka.actor.Actor._

/**
 *
 */
class Boot {
  val west = remote.actorFor("trafficLight-Left", "localhost", 2553)
  val east = remote.actorFor("trafficLight-Right", "localhost", 2553)
  val north = remote.actorFor("trafficLight-Top", "localhost", 2553)
  val south = remote.actorFor("trafficLight-Bottom", "localhost", 2553)

  val commands = actorOf(new JunctionCommands()).start
  val junction = actorOf(new Junction(trafficLights = List(west,east,north,south),listener = commands)).start
  val sensor = actorOf(new Sensor(LANE.WEST,junction)).start
  Actor.remote.register(sensor)
  Actor.remote.start
  //TODO register JunctionCommands and Queries as remote actors and call these from swing app.
  //
}