package de.andlabs.routing

import org.scalatest.junit.JUnitSuite
import scala.collection.mutable.ListBuffer
import scala.util.Random
import org.junit.Assert._
import org.junit.Before
import org.junit.Test
import scala.xml.XML
import Graph.Node

class ReachTestSuite extends JUnitSuite {


  def testReachGraphEdges() {

    println()
    Graph.load("r_")
    
    val edges = new ListBuffer[(Int,Int,Int)]()
    Node(5).foreach_outgoing { (nb, w) =>
      edges += ((5, nb, w))
    }

    Graph.load("")
    for ((a,b,c) <- edges) {
      assertEquals(c, new Dijkstra(a,b).getDist)
    }
}

  @Test def testReachDijkstra() { 
  
  //http://localhost:8080/routes.kml?from=527&to=7212
    
    //testOne(174, 174)
    testOne(97, 12)
    testOne(14, 12)
    
    testOne(43, 22)
    testOne(187, 129)
    testOne(123, 99)
    testOne(72, 157)
    testOne(117, 89)
    

    
    testOne(32, 45)
    testOne(170, 15)
    testOne(105, 123)

    testOne(18, 39)
    testOne(93, 29)
    //testOne(2213, 4923)
  }
  
  def testAnother() {
   
    testOne(34, 32)
    
  }
  
    def randomTesting() { 
    
    for (i <- 0 until 42) testOne(Random.nextInt(189), Random.nextInt(189))
  
  }
  
  def testOne(s: Int, t: Int) {

    println("verifying correctness from "+s+" to "+t+":\n")  
    Graph.load("r_")
    val (dist, path) = new ReachDijkstra(s, t).run
    XML.save("routes.kml", kml.path(dist, path), "UTF-8", true, null)
    Graph.load("")
    val dijkstraDist = new Dijkstra(s, t).run
    assertEquals(dijkstraDist, dist)
    //println("dist: "+dist)
    //println("Dijkstra: "+dijkstraDist+"\n\n\n")
    println("\n")
	
  }

}
