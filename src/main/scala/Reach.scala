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

import java.io.{ File, FileWriter, FileOutputStream, DataOutputStream }
import scala.collection.mutable.{ ArrayBuffer, Map }
import scala.math
import Graph.Node

object Reach {

Graph.load("")
val get = new Array[Int](Graph.node_array.length)
case class Edge(from:Int, to:Int, dist:Int)
val edges = new ArrayBuffer[Edge]()

def main(args: Array[String]) {


  println
  val nodeNmb = Graph.node_array.length-1
  val percent = nodeNmb.toFloat / 10

  print("determining reach values")
  for (i <- 0 until nodeNmb) {
    findReachValues(i)
    if (i % percent < 1) print(".")
  }
  val reach_out = dataOutputStream("reaches.bin")
  val fw = new FileWriter("reaches.txt") 
  for (i <- 0 until nodeNmb) {
    fw.write(Reach.get(i)+"\n")
  }
  fw.close()
  println(" Done. \n")
  
  print("constructing reach graph")
  for (i <- 0 until nodeNmb) {
    addOverlayEdges(i)
    if (i % percent < 1) print(".")
  }
  println(" Done. \n")

  print("writing adjacency array..")
  val node_out = dataOutputStream("r_nodes.bin")
  val edge_out = dataOutputStream("r_edges.bin")
  val dist_out = dataOutputStream("r_dists.bin")

  var i = 0
  var reach_sum = 0
  for (id <- 0 until nodeNmb) {
    node_out.writeInt(i)
    reach_sum = reach_sum + Reach.get(id)
    while (i < edges.size && edges(i).from == id) {
      edge_out.writeInt(edges(i).to)
      dist_out.writeInt(edges(i).dist)
      i = i+1
    }
  }
  node_out.writeInt(i)
  println("Done. ")
  println("(avg degree: "+(edges.size/nodeNmb)+", "+
            "avg reach: "+reach_sum/nodeNmb+")\n\n")

  Graph.load("r_")
  kml.reach()
  println

  }

def addOverlayEdges(source: Int) {

  spt.clear
  val Q = new BinaryHeap()
  val reach = Reach.get(source)
  var node = Node(source, dist = 0)
  Q.insert(node)

  while (!Q.isEmpty) {
  
    node = Q.extractMin // now settled.
    var pred: Node = null
    
    if (node.pred != null) {
      if(Reach.get(node.id) >= reach && node.id != source) {
        edges += Edge(source, node.id, node.dist)
        pred = null // branch covered
      } else pred = node
    } 

    var r = 0
    node.foreach_outgoing { (neighbour , weight) => // relaxation
      
      if (neighbour.dist > node.dist + weight) {
          neighbour.dist = node.dist + weight
          neighbour.pred = pred //predecessor
          r = r+1
    
          if (neighbour.visited) //before
            Q.decreaseKey(neighbour)
          else // first time seen
            Q.insert(neighbour)
      }
    }
    if (r == 0 && node.pred != null) {
      while (Reach.get(node.id) <= reach && node.pred != null) node = node.pred
      val e = Edge(source, node.id, node.dist)
      if (!edges.contains(e) && source != node.id) edges += e
    }
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
