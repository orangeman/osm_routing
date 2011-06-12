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
import scala.collection.mutable.ArrayBuffer
import scala.xml.XML


object kml {
	
val latlon = ArrayBuffer[LatLon]()
case class LatLon(lat:Float, lon:Float)
val in = new DataInputStream(new FileInputStream(new File("latlns.bin")))
while (in.available != 0) { latlon += LatLon(in.readFloat,in.readFloat) }


def write(path: List[Int], file: String) {

  val kml = build(path)
  print("writing kml..")
  XML.save(file, kml, "UTF-8", true, null)
  println(" Done.")

}

def build(path: List[Int]) = {

<kml xmlns="http://www.opengis.net/kml/2.2">
  <Document>
   <Style id="yellowLineStyle">
     <LineStyle>
       <width>42</width>
     </LineStyle>
   </Style>
  { for (node <- path) yield
    <Placemark>
      <name>{node}</name>
      <description>foo bar</description>
      <styleUrl>#yellowLineStyle</styleUrl>
      <Point>
        <coordinates> 
         { latlon(node).lon },{ latlon(node).lat }
        </coordinates>
      </Point>
    </Placemark>
  }     
  </Document>
</kml>

}
}

