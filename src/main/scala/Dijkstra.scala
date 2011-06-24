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

import scala.collection.mutable.{ListBuffer, Map}
import Graph.Node


class Dijkstra(source: Int, target: Int) {


  def run {

    val Q = new BinaryHeap()
    Q.insert(Node(source, dist = 0))

    while (!Q.isEmpty) {

      var node = Q.extractMin // now settled.

      if (node.id == target) { //are we allready done?
        println("PATH FOUND (searched "+spt.size+" nodes)")
        return
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
    }
    println("NO PATH FOUND! (searched "+spt.size+" nodes)")
  }

	
  def getPath = {
    run // the algorithm
    var node: Node = target
    val path = ListBuffer[Int]()
    while (node.pred != null) {
      path += node.id //
      node = node.pred
    }
    path += node.id
    path.toList
  }


  def getDist = {
    run // alg
    target.dist
  }





	// some magic
	var spt: Map[Int, Node] = Map[Int, Node]()
	implicit def getSPTNode(id: Int): Node = { 
    spt.get(id) match { // even more magic
     case Some(node) => node; case None => 
       val nd = Node(id); spt(id) = nd; nd
  }} 
}

