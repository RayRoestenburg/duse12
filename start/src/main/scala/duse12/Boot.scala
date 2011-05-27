package duse12

import akka.actor.Actor
import akka.actor.Actor._

/**
 *
 */
class Boot {
  //  TODO think about if supervision other than default supervision is necessary for workshop
  //  val factory = SupervisorFactory(
  //    SupervisorConfig(
  //      OneForOneStrategy(List(classOf[Exception]), 3, 100),
  //      Supervise(
  //        actorOf[RootEndpoint],
  //        Permanent) ::
  //      Supervise(
  //        actorOf[Sensor],
  //        Permanent)
  //      :: Nil))
  //  factory.newInstance.start
  // start the sensors for the junction
  val junction = actorOf(new Junction()).start
  val sensor = actorOf(new Sensor(1, junction)).start
  Actor.remote.register(sensor)
  Actor.remote.start
}