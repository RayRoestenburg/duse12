package duse12

import akka.actor.Actor
import akka.actor.Actor._

/**
 *
 */
class Boot {
  val left = remote.actorFor("trafficLight-Left", "localhost", 2553)
  val right = remote.actorFor("trafficLight-Right", "localhost", 2553)
  val top = remote.actorFor("trafficLight-Top", "localhost", 2553)
  val bottom = remote.actorFor("trafficLight-Bottom", "localhost", 2553)

  val commands = actorOf(new JunctionCommands()).start
  val junction = actorOf(new Junction(trafficLights = List(left,right,top,bottom),listener = commands)).start
  val sensor = actorOf(new Sensor(Lane.Left,junction)).start
  Actor.remote.register(sensor)
  Actor.remote.start
  //TODO register JunctionCommands and Queries as remote actors and call these from swing app.
  //
}