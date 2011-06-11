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

import scala.math._
import java.io.File
import scala.xml.XML
import java.io.FileOutputStream
import java.io.DataOutputStream
import scala.collection.mutable.Map
import scala.collection.mutable.ArrayBuffer

object Osm {


  def parse(osm_file: String) {

	val xml = XML.loadFile(osm_file)
	edges = ArrayBuffer[Edge]()
	nodes = Map[Int, Node]()

	print("\n -> reading nodes..")
	(xml \ "node") foreach { (node) =>

		val id = (node\"@id").text.toInt
		val lat = (node\"@lat").text.toFloat
		val lon = (node\"@lon").text.toFloat
		nodes(id) = Node(lat, lon)
	} 
	println("   ("+nodes.size+") Done.")

	print(" -> reading ways...")
	(xml \ "way") foreach { (way) =>
	
		val speed = ((way\"tag" filter { (t) => (t\"@k").text == "highway" })
									\ "@v").text match {
			case "residential" => 20
			case "secondary" => 60
			case "tertiary" => 40
			case "primary" => 80
			case _ => 0
		}
	
		if (speed > 0) {
			val ids = way\"nd" map { (nd) => ((nd\"@ref").text.toInt)}
			for ((u,v) <- ids zip ids.tail) { 
				edges += Edge(u, v, dist(u,v))
				edges += Edge(v, u, dist(v,u))
			}
		}
	}
	println("   ("+edges.size+") Done.")



	print(" -> sorting edges..")
	edges = edges.sortWith(_.from < _.from)
	println("          Done.")



	print(" -> writing file...")
	val osm_id_map = Map[Int, Int]()
	var edge_buf = ArrayBuffer[Int]()
	val node_out = dataOutputStream("nodes.bin")
	val edge_out = dataOutputStream("edges.bin")
	val dist_out = dataOutputStream("dists.bin")
	val latlons = dataOutputStream("latlns.bin")
	var id = 0

	for (e <- edges) { // build adjacency arrays

		if (e.from != id) {
			id = e.from  
			node_out.writeInt(edge_buf.size) 
			osm_id_map(id) = osm_id_map.size
			latlons.writeFloat(nodes(e.from).lat)
			latlons.writeFloat(nodes(e.from).lon)
		}
		dist_out.writeInt(e.dist)  // write dists
		edge_buf += e.to  // collect edge array
	}

	//   replace osm ids with adjacency array ids
	edge_buf = edge_buf map osm_id_map

	//   write edge array to adjacency array file
	edge_buf foreach (edge_out.writeInt)

	println("          Done.\n")

  }



  def dataOutputStream(file: String)
	= new DataOutputStream(new FileOutputStream(new File(file))) 


  def dist(from: Node, to: Node): Int = {
	val lat1 = toRadians(from.lat);
	val lon1 = toRadians(from.lon);
	val lat2 = toRadians(to.lat);
	val lon2 = toRadians(to.lon);
	return ((6378.388f * acos(
		sin(lat1) * sin(lat2)
		+ cos(lat1) * cos(lat2)
		* cos(lon2 - lon1)))*1000).toInt;
  }

  var nodes: Map[Int, Node] = null
  var edges: ArrayBuffer[Edge] = null

  case class Node(lat:Float, lon:Float)
  case class Edge(from:Int, to:Int, dist:Int)

  implicit def Int2Node(id:Int): Node = nodes(id)

  }



