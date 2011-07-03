 /*
  * Copyright 2011 Andlabs, GbR.
  *
  * This is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  *
  * OsmRouting is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * You should have received a copy of the GNU General Public License
  * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
  */

package de.andlabs.routing

import java.io.{ File, FileOutputStream, DataOutputStream }
import scala.collection.mutable.{ ArrayBuffer, Map }
import scala.math
import Graph.Node

object Reach {

Graph.load
val get = new Array[Int](Graph.node_array.length)
case class Edge(from:Int, to:Int, dist:Int)
val edges = new ArrayBuffer[Edge]()

def main(args: Array[String]) {

  println
  
  print("computing reach values..   ")
  for (i <- 0 until Graph.node_array.length-1) {
    findReachValues(i)
  }
  println("Done. \n")
  
  print("constructing reach Graph.. ")
  for (i <- 0 until Graph.node_array.length-1) {
    addOverlayEdges(i)
  }
  println("Done. \n")

  var i = 0
  var edge_buf = ArrayBuffer[Int]()
  val node_out = dataOutputStream("r_nodes.bin")
  val edge_out = dataOutputStream("r_edges.bin")
  val dist_out = dataOutputStream("r_dists.bin")

  for (id <- 0 until Graph.node_array.length-1) {
    while (i < edges.size && edges(i).from == id) {
      edge_out.writeInt(edges(i).to)
      dist_out.writeInt(edges(i).dist)
      i = i+1
    }
    node_out.writeInt(i)
  }

  ReachGraph.load
  kml.reach()
  
  
  println("avg degree: "+(edges.size/(Graph.node_array.length-1)))
  
  
  println

}

def addOverlayEdges(source: Int) {

  spt.clear
  val Q = new BinaryHeap()
  val reach = Reach.get(source)
  var node = Node(source, dist = 0)
  Q.insert(node)

  while (node.dist <= reach && !Q.isEmpty) {
  
    if (Reach.get(node.id) >= reach && node.id != source) {
      edges += Edge(source, node.id, node.dist)
    }

    node.foreach_outgoing { (neighbour , weight) => // relaxation
      if (neighbour.dist > node.dist + weight) {
          neighbour.dist = node.dist + weight
          neighbour.pred = node //predecessor

          if (neighbour.visited) //before
            Q.decreaseKey(neighbour)
          else // first time seen
            Q.insert(neighbour)
      }
    }
    node = Q.extractMin // now settled.
  }
}

def findReachValues(source: Int) {

  spt.clear
  val Q = new BinaryHeap()
  Q.insert(Node(source, dist = 0))
  while (!Q.isEmpty) {

    var node = Q.extractMin // now settled.
    var isFinal = true

    node.foreach_outgoing { (neighbour , weight) => // relaxation

      if (neighbour.dist > node.dist + weight) {
          isFinal = false
          neighbour.dist = node.dist + weight
          neighbour.pred = node //predecessor

          if (neighbour.visited) //before
            Q.decreaseKey(neighbour)
          else // first time seen
            Q.insert(neighbour)

      }
    }
    
    if (isFinal) {
    
      val dist = node.dist
      //println("foo "+node.id+"  "+c)
      while (node.pred != null) {
      get(node.id) = math.max(get(node.id),math.min(node.dist,dist-node.dist))
      node = node.pred
      }
    }
    
  }
}


  def dataOutputStream(file: String)
    = new DataOutputStream(new FileOutputStream(new File(file))) 


	// some magic
	var spt: Map[Int, Node] = Map[Int, Node]()
	implicit def getSPTNode(id: Int): Node = { 
    spt.get(id) match { // even more magic
     case Some(node) => node; case None => 
       val nd = Node(id); spt(id) = nd; nd
  }} 

}
