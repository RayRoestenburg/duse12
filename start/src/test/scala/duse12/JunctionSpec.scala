package duse12

import org.scalatest.{BeforeAndAfterAll, WordSpec}
import org.scalatest.matchers.ShouldMatchers
import akka.testkit.TestKit
import java.util.Date
import akka.util.duration._
import akka.actor.Actor._
import duse12.messages._
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Specifications for the Junction.
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
  val statusSouth = new MockLight()
  val statusEast = new MockLight()

  val lightWest = actorOf(new TrafficLight(LANE.WEST,statusWest)).start
  val lightNorth = actorOf(new TrafficLight(LANE.NORTH,statusNorth)).start
  val lightEast = actorOf(new TrafficLight(LANE.EAST,statusEast)).start
  val lightSouth = actorOf(new TrafficLight(LANE.SOUTH,statusSouth)).start
  val queries = actorOf(new JunctionQueries()).start
  val lights = List(lightWest, lightNorth, lightEast, lightSouth)
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
        def createLane(lane: LANE.HEADING, start: Int, size: Int) = List.iterate(VehicleQueued(start, lane, newDate), size)(newId _)

        junction ! ResetJunction()
        expectMsg(ResetJunction())
        val westLane = createLane(LANE.WEST, 1, 10)
        expectLane(westLane)
        val northLane = createLane(LANE.NORTH, 11, 11)
        expectLane(northLane)
        val southLane = createLane(LANE.SOUTH, 23, 7)
        expectLane(southLane)
        val eastLane = createLane(LANE.EAST, 31, 9)
        expectLane(eastLane)
        junction ! ControlTraffic()
        expectMsg(JunctionDecision(LANE.NORTH))
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
        expectMsg(JunctionDecision(LANE.SOUTH))
        junction ! ControlTraffic()
        expectMsg(JunctionDecision(LANE.WEST))
        junction ! ControlTraffic()
        expectMsg(JunctionDecision(LANE.NORTH))
      }
    }
  }
}