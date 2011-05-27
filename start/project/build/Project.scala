import sbt._
import de.element34.sbteclipsify._
import com.github.olim7t.sbtscalariform._

class JunctionProject(info: ProjectInfo) extends DefaultProject(info) 
with Eclipsify with IdeaProject with AkkaKernelProject with ScalariformPlugin {
 override def repositories = Set("akka" at "http://akka.io/repository")
 val akkaRemote = akkaModule("remote")
 val akkaHttp = akkaModule("http")
 val akkaCamel = akkaModule("camel")
 lazy val logback = "ch.qos.logback" % "logback-classic" % "0.9.28" % "runtime"
 val swing = "org.scala-lang" % "scala-swing" % this.buildScalaVersion % "compile"
 override def formatBeforeCompiling = false
}

