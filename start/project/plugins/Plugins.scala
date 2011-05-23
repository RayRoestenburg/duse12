import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
  val akkaRepo   = "Akka Repo" at "http://akka.io/repository"
  val akkaPlugin = "se.scalablesolutions.akka" % "akka-sbt-plugin" % "1.1"
  // eclipsify plugin
  lazy val eclipse = "de.element34" % "sbt-eclipsify" % "0.7.0"
  val sbtIdeaRepo = "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"
  val sbtIdea = "com.github.mpeltonen" % "sbt-idea-plugin" % "0.4.0"
  val bumRepo = "Bum Networks Release Repository" at "http://repo.bumnetworks.com/releases"
  val sbtAkkaBivy = "net.evilmonkeylabs" % "sbt-akka-bivy" % "0.2.0"
}
