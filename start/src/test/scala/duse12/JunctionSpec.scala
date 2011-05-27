package duse12

import org.scalatest.{BeforeAndAfterAll, WordSpec}
import org.scalatest.matchers.ShouldMatchers
import akka.testkit.TestKit
import java.util.Date
import akka.util.duration._
import akka.actor.Actor._

/**
 *
 */
class JunctionSpec extends WordSpec with BeforeAndAfterAll with ShouldMatchers with TestKit {
  val lightLeft = actorOf(new TrafficLight(Lane.Left)).start
  val lightTop = actorOf(new TrafficLight(Lane.Top)).start
  val lightRight = actorOf(new TrafficLight(Lane.Right)).start
  val lightBottom = actorOf(new TrafficLight(Lane.Bottom)).start
  val queries = actorOf(new JunctionQueries()).start
  val lights = List(lightLeft,lightTop, lightRight, lightBottom)
  val commands = actorOf(new JunctionCommands()).start
  val junction = actorOf(new Junction(trafficLights = lights, listener = testActor)).start

  override protected def afterAll(): scala.Unit = {
    stopTestActor
    junction.stop
    queries.stop
    for(light<-lights){
      light.stop
    }
    commands.stop
  }
  "The Junction" should {
    "forward VehicleQueued messages to the listener " in {
      within(500 millis) {
        val msg = VehicleQueued(1,Lane.Left,new Date(System.currentTimeMillis()))
        junction ! msg
        expectMsg(msg)
      }
    }
    "control traffic when a ControlTraffic command is received and forward a JunctionDecision" in {
      within(500 millis) {
        val msg = ControlTraffic()
        junction ! msg
        expectMsg(JunctionDecision(Lane.Left))
      }
    }
  }
}