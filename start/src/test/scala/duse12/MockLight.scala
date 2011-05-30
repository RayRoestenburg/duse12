package duse12

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.CyclicBarrier

/**
 * Mock imp of switch light to be used in tests
 */
class MockLight(barrier:Option[CyclicBarrier] = None) extends LightSwitch {
  val state = new AtomicBoolean(false)

  override def switchToRed {
    state.set(false)
    barrier.foreach(_.await())
  }

  override def switchToGreen {
    state.set(true)
    barrier.foreach(_.await())
  }
}



