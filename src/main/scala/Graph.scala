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

import java.io.{File, FileInputStream, DataInputStream}
import scala.collection.mutable.{Map, ArrayBuffer}

object Graph {

  val node_array = ArrayBuffer[Int]()
  val edge_array = ArrayBuffer[Int]()
  val dist_array = ArrayBuffer[Int]()

  def load() {
    print("loading graph..")
    val nodes = new DataInputStream(new FileInputStream(new File("nodes.bin")))
    while (nodes.available != 0) { node_array += nodes.readInt } 
    val edges = new DataInputStream(new FileInputStream(new File("edges.bin")))
    while (edges.available != 0) { edge_array += edges.readInt } 
    val dists = new DataInputStream(new FileInputStream(new File("dists.bin")))
    while (dists.available != 0) { dist_array += dists.readInt } 
    println("  ("+node_array.size+" nodes, "+edge_array.size+" edges) Done.\n")
  }


  case class Node(val id: Int, var dist: Int, var pred: Node, var index: Int) {

    def visited = index > 0
    //def settled = index == -1
    //def relaxed = index >= +1

    def foreach_outgoing(fun: (Int,Int) => Unit) {
        for (i <- node_array(id) until node_array(id+1)) {
          fun(edge_array(i), dist_array(i))  // call function foreach neighbour
        }
    }
  }

  object Node {
    def apply(id: Int, dist: Int = Int.MaxValue)  = new Node(id, dist, null, 0)
  }

}
