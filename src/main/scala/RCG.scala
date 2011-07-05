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
import scala.collection.mutable.{Map, ArrayBuffer, ListBuffer}
import Graph.Node


class ReachDijkstra(source: Int, target: Int) {

  var forward = true
  var fExp = 0
  var bExp = 0
  val fQ = new BinaryHeap()
  val bQ = new BinaryHeap()
	var fspt: Map[Int, Node] = Map[Int, Node]()
  var bspt: Map[Int, Node] = Map[Int, Node]()

  def run: (Int, List[(Int, Int, List[Int])]) = {

    val s = Node(source, dist = 0)
    val t = Node(target, dist = 0)
    fspt(s.id) = s; bspt(t.id) = t
    fQ.insert(s); bQ.insert(t)

    val vias = ListBuffer[(Int, Int)]()

    var i = 0
    var min = 99999
    while (!fQ.isEmpty || !bQ.isEmpty) { 

      i = i+1    
      if ((fQ.size < bQ.size && !fQ.isEmpty) || bQ.isEmpty)
        forward = true
      else
        forward = false

      var node = Q.extractMin // now settled.
      //println(i+": "+(if (forward) " --> " else " <-- ")+" settled node "+node.id+" (dist="+node.dist+")")

      if (other(node.id).settled) {
        if (other(node.id).dist+node.dist < min) min = other(node.id).dist+node.dist
        //fQ.stats; bQ.stats
        //println("PATH FOUND (met at "+node.id+", dist: "+(other(node.id).dist+node.dist)+", searched "+(fspt.size+bspt.size)+" nodes) "+fExp+"  "+bExp)
        vias += ((node.id, other(node.id).dist + node.dist))
        if (fExp > 2* min && bExp > 2* min) {
          //println("PATH FOUND: dist="+min+"   (searched "+(fspt.size+bspt.size)+" nodes, found "+vias.size+" vias)\n")
          for ((via, dist) <- vias) {
            var n: Node = via
            while (n.pred != null) { n.det = math.min(dist-min, n.det); n = n.pred }
            n = other(via)
            while (n.pred != null) { n.det = math.min(dist-min, n.det); n = n.pred }
          }
          val routes = ListBuffer[(Int, Int, List[Int])]()
          routes += ((source, target, null))
          for ((via, dist) <- vias) {
            var p = ListBuffer[Int]()
            var n: Node = via
            while (n.pred != null && n.pred.det == n.det) { p += n.pred.id; n = n.pred }
            if (n.pred != null) p += n.pred.id
            p = p.reverse
            p += via
            n = other(via)
            while (n.pred != null && n.pred.det == n.det) { p += n.pred.id; n = n.pred }
            if (n.pred != null) p += n.pred.id
            if (!p.isEmpty)
              routes += ((via, via.det, p.toList))
          }
          return (min, routes.toList)
        }
      }

      var c = 0
      var r = 0 
      node.foreach_outgoing { (neighbour , weight) => // relaxation
        c = c+1
        
        if (neighbour.dist > node.dist + weight) {
            r = r+1
            
            neighbour.dist = node.dist + weight
            neighbour.pred = node //predecessor

            if (neighbour.visited) //before
              Q.decreaseKey(neighbour)
            else // first time seen
              Q.insert(neighbour)

        }
      }
      //println("         checked "+c+" neighbours, relaxed "+r+" \n")
      
    }
    fQ.stats; bQ.stats
    println("PATH FOUND (full): dist="+min+"   (searched "+(fspt.size+bspt.size)+" nodes, found "+vias.size+" vias)\n")
    for ((via, dist) <- vias) {
            var n: Node = via
            while (n.pred != null) { n.det = math.min(dist-min, n.det); n = n.pred }
            n = other(via)
            while (n.pred != null) { n.det = math.min(dist-min, n.det); n = n.pred }
          }
          val routes = ListBuffer[(Int, Int, List[Int])]()
          routes += ((source, target, null))
          for ((via, dist) <- vias) {
            var p = ListBuffer[Int]()
            var n: Node = via
            while (n.pred != null && n.pred.det == n.det) { p += n.pred.id; n = n.pred }
            if (n.pred != null) p += n.pred.id
            p = p.reverse
            p += via
            n = other(via)
            while (n.pred != null && n.pred.det == n.det) { p += n.pred.id; n = n.pred }
            if (n.pred != null) p += n.pred.id
            if (!p.isEmpty)
              routes += ((via, via.det, p.toList))
          }
          return (min, routes.toList)
  }

  def Q = {
    if (forward) {
      if (!fQ.isEmpty) fExp = fQ.getMin
      fQ
    } else {
      if (!bQ.isEmpty) bExp = bQ.getMin
      bQ
    }
  }
  
  def other(id: Int): Node = {
    val spt = if (forward) bspt else fspt
    spt.get(id) match {
     case Some(node) => node; case None => 
       val nd = Node(id); spt(id) = nd; nd
	}}
	
	// some magic
	implicit def getSPTNode(id: Int): Node = {
	  val spt = if (forward) fspt else bspt
    spt.get(id) match { // even more magic
     case Some(node) => node; case None => 
       val nd = Node(id); spt(id) = nd; nd
  }} 


  def getDist = {
    target.dist
  }





}
