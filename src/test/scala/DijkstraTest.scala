package de.andlabs.routing

import net.pragyah.scalgorithms.heaps.FibonacciHeap
import org.scalatest.junit.JUnitSuite
import scala.collection.mutable.ListBuffer
import org.junit.Assert._
import org.junit.Test
import org.junit.Before
import Graph.Node

class DijkstraTestSuite extends JUnitSuite {


  @Test def testDijkstra() { 

	val path = Dijkstra(1296, 15475)//303137)//7209)
	//assertEquals(path.size, 100)
	kml.write(path, "output.kml")
	
  }

}
