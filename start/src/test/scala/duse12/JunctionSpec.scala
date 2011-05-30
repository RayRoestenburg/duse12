package duse12

import org.scalatest.{BeforeAndAfterAll, WordSpec}
import org.scalatest.matchers.ShouldMatchers
import akka.testkit.TestKit
import java.util.Date
import akka.util.duration._
import akka.actor.Actor._
import java.util.concurrent.atomic.AtomicBoolean
import duse12.messages._

/**
 * Specifications for the Junction.
 * TODO add specs for Sensor, TrafficLight, JunctionCommands, JunctionQueries
 */
class JunctionSpec extends WordSpec with BeforeAndAfterAll with ShouldMatchers with TestKit {

  class MockLight extends LightSwitch {
    val state = new AtomicBoolean(false)

    def isGreen = state.get

    override def switchToRed {
      state.set(false)
    }

    override def switchToGreen {
      state.set(true)
    }
  }

  val statusWest = new MockLight()
  val statusNorth = new MockLight()
  val statusEast = new MockLight()

  val lightWest = actorOf(new TrafficLight(LANE.WEST, statusWest)).start
  val lightNorth = actorOf(new TrafficLight(LANE.NORTH, statusNorth)).start
  val lightEast = actorOf(new TrafficLight(LANE.EAST, statusEast)).start
  val queries = actorOf(new JunctionQueries()).start
  val lights = List(lightWest, lightNorth, lightEast)
  val commands = actorOf(new JunctionCommands()).start
  val junction = actorOf(new Junction(trafficLights = lights, listener = testActor)).start

  override protected def afterAll(): scala.Unit = {
    junction.stop
    lights.foreach(_.stop)
    commands.stop
    queries.stop
    stopTestActor
  }

  def newDate = new Date(System.currentTimeMillis())

  "The Junction" should {
    "increment a queue count for every lane and forward VehicleQueued messages to the listener " in {
      within(500 millis) {
        val msg = VehicleQueued(1, LANE.WEST, newDate)
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
    "decrement queue count for every lane and forward VehiclePassed messages to the listener" in {
      within(500 millis) {
        val msg = VehiclePassed(1, LANE.WEST, newDate)
        junction ! msg
        expectMsg(msg)
      }
    }
    "reset queue count for every lane and forward ResetJunction messages to the listener" in {
      within(500 millis) {
        junction ! ResetJunction()
        expectMsg(ResetJunction())
      }
    }
    "decide on lane which has most queued vehicles compared all other lanes" in {
      within(500 millis) {
        def expectLane(lane: List[VehicleQueued]) = {
          for (q <- lane) {
            junction ! q
            expectMsg(q)
          }
        }
        def newId(q: VehicleQueued): VehicleQueued = q.copy(id = q.id + 1)
        def fillLane(lane: LANE.HEADING, start: Int, size: Int) = List.iterate(VehicleQueued(start, lane, newDate), size)(newId _)

        junction ! ResetJunction()
        expectMsg(ResetJunction())
        val westLane = fillLane(LANE.WEST, 1, 10)
        expectLane(westLane)
        val northLane = fillLane(LANE.NORTH, 11, 11)
        expectLane(northLane)
        val eastLane = fillLane(LANE.EAST, 31, 9)
        expectLane(eastLane)
        junction ! ControlTraffic()
        expectMsg(JunctionDecision(LANE.NORTH))
      }
    }
    "should pick the next maximum queued lane vehicles have passed on the decided lane" in {
      within(500 millis) {
        // let all cars pass from north
        for (i <- 11 until 22) {
          val p = VehiclePassed(i, LANE.NORTH)
          junction ! p
          expectMsg(p)
        }
        // decision should be on lane that is no maximum
        junction ! ControlTraffic()
        expectMsg(JunctionDecision(LANE.WEST))
      }
    }
    "Decide on Clockwise lane starting with NORTH when there are no vehicles at the junction" in {
      within(500 millis) {
        junction ! ResetJunction()
        expectMsg(ResetJunction())
        junction ! ControlTraffic()
        expectMsg(JunctionDecision(LANE.NORTH))
        junction ! ControlTraffic()
        expectMsg(JunctionDecision(LANE.EAST))
        junction ! ControlTraffic()
        expectMsg(JunctionDecision(LANE.WEST))
        junction ! ControlTraffic()
        expectMsg(JunctionDecision(LANE.NORTH))
      }
    }
  }
}