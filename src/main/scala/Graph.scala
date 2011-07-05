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

  var node_array: ArrayBuffer[Int] = null
  var edge_array: ArrayBuffer[Int] = null
  var dist_array: ArrayBuffer[Int] = null

  def load(f: String) {
    
    print("loading graph..")
    node_array = ArrayBuffer[Int]()
    edge_array = ArrayBuffer[Int]()
    dist_array = ArrayBuffer[Int]()
    val nod = new DataInputStream(new FileInputStream(new File(f+"nodes.bin")))
    while (nod.available != 0) { node_array += nod.readInt } 
    val edg = new DataInputStream(new FileInputStream(new File(f+"edges.bin")))
    while (edg.available != 0) { edge_array += edg.readInt } 
    val dis = new DataInputStream(new FileInputStream(new File(f+"dists.bin")))
    while (dis.available != 0) { dist_array += dis.readInt } 
    println("  ("+node_array.size+" nodes, "+edge_array.size+" edges)   Done.")
  }


  case class Node(val id: Int, var dist: Int, var pred: Node, var index: Int, var det: Int) {

    def visited = index != 0
    def settled = index == -1
    //def relaxed = index >= +1

    def foreach_outgoing(fun: (Int,Int) => Unit) {
        for (i <- node_array(id) until node_array(id+1)) {
          fun(edge_array(i), dist_array(i))  // call function foreach neighbour
        }
    }
  }

  object Node {
    def apply(id: Int, dist: Int = Int.MaxValue)  = new Node(id, dist, new Node(id, dist, null, 0, Int.MaxValue), 0, Int.MaxValue)
  }

}
