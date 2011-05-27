package duse12

import akka.actor.Actor
import akka.actor.Actor._

/**
 *
 */
class Boot {
  val trafficLight = remote.actorFor("trafficLight-1", "localhost", 2553)

  val junction = actorOf(new Junction(List(trafficLight))).start
  val sensor = actorOf(new Sensor(1, junction)).start
  Actor.remote.register(sensor)
  Actor.remote.start
  //TODO register JunctionCommands and Queries as remote actors and call these from swing app.
  //
}