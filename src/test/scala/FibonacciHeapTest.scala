package de.andlabs.routing

import net.pragyah.scalgorithms.heaps.FibonacciHeap
import org.scalatest.junit.JUnitSuite
import scala.collection.mutable.ListBuffer
import org.junit.Assert._
import org.junit.Test
import org.junit.Before
import Graph.Node

class HeapTestSuite extends JUnitSuite {


  @Before def initialize() {
	println("init")
  }

  @Test def testFibonacciHeap() { 
	val heap = new FibonacciHeap[Node](Node.min)
	heap += new Node(1, 3, 0, false)
	heap += new Node(2, 5, 0, false)
	heap += new Node(3, 1, 0, false)
	val n = new Node(4 ,7 ,0, false)
	heap += n 
	heap.decreaseKey(n, n.copy(dist = 0))
    assertEquals(heap.extractMin.get.dist, 0)
	println("heap:"+heap)
    assertEquals(heap.extractMin.get.dist, 1)
	println("heap:"+heap)
    assertEquals(heap.extractMin.get.dist, 3)
    assertTrue(true)
  }

  implicit def int2Node(id: Int): Node = { 
		new Node(id, Int.MaxValue, 0, false)
  }

  @Test def verifyFun() { 
	println("fun :-)")
  }
}
