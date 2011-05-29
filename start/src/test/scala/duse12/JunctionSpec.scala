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
  val lightWest = actorOf(new TrafficLight(LANE.WEST)).start
  val lightNorth = actorOf(new TrafficLight(LANE.NORTH)).start
  val lightEast = actorOf(new TrafficLight(LANE.EAST)).start
  val lightSouth = actorOf(new TrafficLight(LANE.SOUTH)).start
  val queries = actorOf(new JunctionQueries()).start
  val lights = List(lightWest,lightNorth, lightEast, lightSouth)
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
        val msg = VehicleQueued(1,LANE.WEST,new Date(System.currentTimeMillis()))
        junction ! msg
        expectMsg(msg)
      }
    }
    "control traffic when a ControlTraffic command is received and forward a JunctionDecision" in {
      within(500 millis) {
        val msg = ControlTraffic()
        junction ! msg
        expectMsg(JunctionDecision(LANE.WEST))
      }
    }
  }
}