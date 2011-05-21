import sbt._

class JunctionProject(info: ProjectInfo) extends DefaultProject(info) with AkkaProject
{
 override def repositories = Set("akka" at "http://akka.io/repository")
}
