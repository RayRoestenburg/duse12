import sbt._
import de.element34.sbteclipsify._

class JunctionProject(info: ProjectInfo) extends DefaultProject(info) with Eclipsify with AkkaProject {
 override def repositories = Set("akka" at "http://akka.io/repository")
 val akkaKernel = akkaModule("kernel")
 val akkaRemote = akkaModule("remote")
 val akkaHttp = akkaModule("http")
 val akkaCamel = akkaModule("camel")
}

