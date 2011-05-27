import sbt._
import de.element34.sbteclipsify._

class JunctionProject(info: ProjectInfo) extends DefaultProject(info) with Eclipsify with AkkaKernelProject {
 override def repositories = Set("akka" at "http://akka.io/repository")
 val akkaRemote = akkaModule("remote")
 val akkaHttp = akkaModule("http")
 val akkaCamel = akkaModule("camel")
 lazy val logback = "ch.qos.logback" % "logback-classic" % "0.9.28" % "runtime"
}

