package duse12


/**
 * Enumeration for identification of Lanes
 */
object LANE extends Enumeration {
  type HEADING = Value
  val NORTH = Value("NORTH")
  val SOUTH = Value("SOUTH")
  val EAST = Value("EAST")
  val WEST = Value("WEST")
  def isVertical(v:LANE.HEADING) = (v == EAST || v == WEST)
  def isHorizontal(v:LANE.HEADING) = (v == NORTH || v == SOUTH)
}










