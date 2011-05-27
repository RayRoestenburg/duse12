import sbt._
import de.element34.sbteclipsify._
import com.github.olim7t.sbtscalariform._

class JunctionProject(info: ProjectInfo) extends DefaultProject(info) 
with Eclipsify with IdeaProject with AkkaKernelProject with ScalariformPlugin {
 override def repositories = Set("akka" at "http://akka.io/repository", "repo1" at "http://repo1.maven.org/maven2")
 val akkaRemote = akkaModule("remote")
 val akkaHttp = akkaModule("http")
 val akkaCamel = akkaModule("camel")
 val akkaTestKit = akkaModule("testkit")
 lazy val logback = "ch.qos.logback" % "logback-classic" % "0.9.28" % "runtime"
 // for ui
 val swing = "org.scala-lang" % "scala-swing" % this.buildScalaVersion % "compile"

 val scalatest = "org.scalatest" % "scalatest_2.9.0" % "1.4.1" % "test->default"
 val junit = "junit" % "junit" % "3.8.1" % "test->default"

 override def formatBeforeCompiling = false
}

