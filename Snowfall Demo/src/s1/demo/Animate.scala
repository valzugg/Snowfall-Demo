package s1.demo

import scala.swing.SimpleSwingApplication
import scala.swing.Swing._ 
import scala.swing.Orientation
import scala.collection.mutable.Buffer
import scala.swing.event._
import scala.swing._
import javax.swing.UIManager
import scala.swing.Alignment

/**
 * Animate inherits SimpleSwingApplication which makes
 * this a graphical user interface
 */
object Animate extends SimpleSwingApplication {

  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)
  
  val area = new DemoArea(Buffer(Snowfall))
  
  val sliderMoreSnow = new Slider()
  val marbles  = new Button("    Turn into marbles!    ")
  val moreWind  = new Button("          More Wind!        ")
  val moreSnow = new Button("          More Snow!        ")
  val reset    = new Button("   Reset (Might Crash)   ")
  
  val everything = new BoxPanel(Orientation.Horizontal)
  val nappulat    = new BoxPanel(Orientation.Vertical)
  nappulat.contents += moreSnow
  nappulat.contents += moreWind
  nappulat.contents += marbles
  nappulat.contents += reset
  
  everything.contents += area
  everything.contents += nappulat
  
  val top = new Frame() {
    preferredSize = (1100, 800)    
    contents = everything
  }

  top.title = "Snowfall"
  top.visible = true
  top.resizable = false
  
  
  this.listenTo(moreSnow)
  this.listenTo(moreWind)
  this.listenTo(marbles)
  this.listenTo(reset)
  
  this.reactions += {
    case clicked: ButtonClicked =>
      val source = clicked.source
      
      if (source==moreSnow && Snowfall.extraSnow != Snowfall.maxSnow) {
        Snowfall.extraSnow += 1
      } else if (source==moreSnow) {
        Dialog.showMessage(area, "Enough snow already!")
      }
      
      if (source==moreWind && Snowfall.wind < Snowfall.maxWind) {
        Snowfall.wind += 0.1
      } else if (source==moreWind) {
        Dialog.showMessage(area, "Enough wind already!")
      }
      
      if (source==marbles && Snowfall.marbles==false) {
        Snowfall.makeMarbles()
      } else if (source==marbles) {
        Snowfall.makeSnow()
      }
      
      if (source==reset) {
        Snowfall.reset() //Can cause NullPointer!!!
      }
      
  }
  
  
  // This sets the delay between frames
  area.startAnimating(10)
}