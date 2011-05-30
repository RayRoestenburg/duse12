package duse12

import org.scalatest.{BeforeAndAfterAll, WordSpec}
import org.scalatest.matchers.ShouldMatchers
import akka.actor.Actor._
import akka.util.duration._
import duse12.messages.GreenLight
import scala.Some
import akka.testkit.TestKit
import java.util.concurrent.{TimeUnit, TimeoutException, CyclicBarrier}

/**
 * Specs for TrafficLight Actor.
 * Use a CyclicBarrier and the MockLight in this test.
 */
class TrafficLightSpec extends WordSpec with BeforeAndAfterAll with ShouldMatchers with TestKit {
  val barrier = new CyclicBarrier(2);
  val mockLight = new MockLight(Some(barrier))

  val light = actorOf(new TrafficLight(LANE.WEST, mockLight)).start

  override protected def afterAll(): scala.Unit = {
    light.stop
    stopTestActor
  }

  "The TrafficLight" should {
    "switch the light to green on a GreenLight message that matches the lane of the TrafficLight if the light is not already green " in {
      within(500 millis) {
        mockLight.state.get() should be(false)
        barrier.reset()
        light ! GreenLight(LANE.WEST)
        barrier.await
        mockLight.state.get() should be(true)
        barrier.reset()
        light ! GreenLight(LANE.WEST)
        try {
          barrier.await(250, TimeUnit.MILLISECONDS)
        } catch {
          case e: TimeoutException => mockLight.state.get() should be(true)
        }
      }
    }
    "switch the light to red on a GreenLight message that does not match the lane of the TrafficLight if the light is not already red " in {
      within(500 millis) {
        mockLight.state.get() should be(true)
        barrier.reset()
        light ! GreenLight(LANE.EAST)
        barrier.await
        mockLight.state.get() should be(false)
        barrier.reset()
        light ! GreenLight(LANE.EAST)
        try {
          barrier.await(250, TimeUnit.MILLISECONDS)
        } catch {
          case e: TimeoutException => mockLight.state.get() should be(false)
        }
      }
    }
  }
}