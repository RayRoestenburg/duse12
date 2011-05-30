package duse12

import org.scalatest.{BeforeAndAfterAll, WordSpec}
import org.scalatest.matchers.ShouldMatchers
import akka.testkit.TestKit
import akka.actor.Actor._
import akka.util.duration._
import scala.Some
import duse12.messages.{DecisionsResponse, DecisionsRequest, JunctionDecision}
/**
 * Spec for JunctionCommands
 */
class JunctionQueriesSpec extends WordSpec with BeforeAndAfterAll with ShouldMatchers with TestKit {

  val commands = actorOf(new JunctionQueries(listener = Some(testActor))).start

  override protected def afterAll(): scala.Unit = {
    commands.stop
    stopTestActor
  }

  "The JunctionQueries" should {
    "return a history list of decisions made" in {
      within(500 millis) {
        commands ! JunctionDecision(LANE.WEST)
        commands ! JunctionDecision(LANE.EAST)
        commands ! JunctionDecision(LANE.NORTH)
        expectMsg(JunctionDecision(LANE.WEST))
        expectMsg(JunctionDecision(LANE.EAST))
        expectMsg(JunctionDecision(LANE.NORTH))
        commands !! DecisionsRequest() match {
          case Some(response:DecisionsResponse) => {
            response.history(0) should be (JunctionDecision(LANE.WEST))
            response.history(1) should be (JunctionDecision(LANE.EAST))
            response.history(2) should be (JunctionDecision(LANE.NORTH))
          }
          case None => fail("no response")
        }
      }
    }
  }
}