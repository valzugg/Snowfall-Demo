package s1.demo

import scala.collection.mutable.Buffer
import java.awt.image.BufferedImage
import java.awt.BasicStroke
import s1.image.ImageExtensions._
import java.awt.Font
import java.awt.Color._
import javax.imageio.ImageIO
import scala.math._


//Determines the circle's size, color, speed and movement
class Circle(var x: Double, var y: Double, val size: Int) { 
  val random       = scala.util.Random
  val randomSin    = random.nextDouble * 0.03
  val speed        = this.size * 0.05 + 0.1 + random.nextDouble * 0.1
  val tone         = min((this.size * (1.0/13) * 105).toInt + 150, 255)
  val transparency = min((1/this.size * 105).toInt + 150, 255)
  val color        = new java.awt.Color(tone,tone,tone,transparency)
  
  def marbleTone   = scala.util.Random.nextInt(255)
  val marbles      = new java.awt.Color(marbleTone,marbleTone,marbleTone,255)
}

/**Represents a snowfall animation*/
object Snowfall extends Effect(1000, 800) {
  
  val random = scala.util.Random
  val snowflakes = Buffer[Circle]() //this is where all the snowflakes are
  
  val normalBG   = Some(ImageIO.read(new java.io.File("pictures/tausta.png")))
  val background = normalBG //Setting the background image
  
  var timer = 0        //Keeps track of the frame number
  var snowiness = 0.05 //Makes it snow gradually more at the start

  val marbleSpeed = 15
  
  var wind = 0.0       //The amount of wind, this is added on when pressing "More Wind!"
  val maxWind = 1.5
  
  var extraSnow = 0    //Adds more snow the higher the number, is added on when pressing "More Snow!"
  val maxSnow = 8
  
  var marbles = false  //If true turns the snowflakes into marbles of different colors
  
  var m = 0 //a multiplier that makes more flakes (when marbles = true) 
  
  /**Adds new snowflakes depending on how much it's snowing
    *It snows more and more gradually (until a maximum amount is reached)*/
  def generateSnow() = {
    if (timer % max((1/snowiness).toInt,1) == 0) {
      for (i <- 0 to (extraSnow*(m + 1)) + m) {
        snowflakes += new Circle(random.nextInt(width * 2) - width, -20, randomSize)
      }
    }
  }
  
  /**Removes the snowflakes when they are below the bottom of the window*/
  def removeOutside() = {
    snowflakes --= snowflakes.filter(_.y > height + 15)
  }
  
  /**Moves the snowflakes down and from side to side using a sine function
    *All movement has been compensated to make the further away movement slower
    *If marbles = true, the speed down is 15 times faster*/
  def moveSnow() = {
    //y-axis movement
    if (!marbles) {
      snowflakes.foreach(c => c.y += c.speed) 
    } else {
      snowflakes.foreach(c => c.y += c.speed * marbleSpeed) 
    }
    
    //x-axis movement
    snowflakes.foreach(t => t.x += wind*t.size*0.5 + t.speed * 0.5 * sin(timer*t.randomSin))
  }
  
  
  //turns the snowflakes into marbles by
  //changeing the colors, transparency and speed of the circles
  //Also makes more circles since they are now moving faster
  def makeMarbles() = {
    Snowfall.marbles = true
    Snowfall.m = marbleSpeed
  }
  
  //turns the marbles into snowflakes by
  //changeing the colors, transparency and speed of the circles
  def makeSnow() = {
    Snowfall.marbles = false
    Snowfall.m = 0
  }
  
  //Resets the state of the demo
  def reset() = {
    marbles = false
    extraSnow = 0
    snowiness = 0.00
    timer = 0
    wind  = 0
    snowflakes.clear() //This can cause a NullPointer error
  }
  
  /**A weighed random generator to make more snowflakes further away (smaller) for realism's sake*/
  def randomSize = {
    if (random.nextBoolean && random.nextBoolean && random.nextBoolean && random.nextBoolean) {
      random.nextInt(5) + 8
    } else if (random.nextBoolean && random.nextBoolean && random.nextBoolean) {
      random.nextInt(3) + 5
    } else if (random.nextBoolean && random.nextBoolean) {
      random.nextInt(3) + 2
    } else {
      random.nextInt(2) + 1
    }
  }
  
  //The things that are changed every frame
  def changeThings() = {
    timer += 1
    
    //Makes the snowing more gradual at the start
    if (timer%10 == 0) snowiness += 0.01
    
    generateSnow()
    moveSnow()
    removeOutside()
  }
  
  //The drawn image every frame
  override def makePic(): BufferedImage = { 
    val pic = emptyImage
    val g = pic.graphics
    background.foreach(image => g.drawImage(image, 0, 0, null))
    
    //This part draws all the snowflakes
    for (s <- snowflakes) {
      if (!marbles) { //Changes the look of the flakes depending on if they are marbles or not
        g.setColor(s.color)
        g.fillOval((s.x).toInt, (s.y).toInt, s.size, s.size)
      } else {
        g.setColor(s.marbles)
        g.fillOval((s.x).toInt, (s.y).toInt, s.size, s.size)
      }
    }
    pic
  }
  
  def next = false
  
}