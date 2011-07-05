package de.andlabs.routing

import org.scalatest.junit.JUnitSuite
import scala.collection.mutable.ListBuffer
import org.junit.Assert._
import org.junit.Test
import org.junit.Before
import Graph.Node

class DijkstraTestSuite extends JUnitSuite {


  @Test def testDijkstra() { 

	val path = new Dijkstra(273, 1428).getPath//303137)//7209)
	//assertEquals(path.size, 100)
	kml.write(path, "output.kml")
	
  }

}
