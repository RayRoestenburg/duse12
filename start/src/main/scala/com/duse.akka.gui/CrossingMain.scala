package com.duse.akka.gui

import scala.swing._

object CrossingMain extends SimpleSwingApplication {
def top = new MainFrame {
title = "Scala TrafficLight App"
contents = new Button {
text = "Click me"
}
}
}
