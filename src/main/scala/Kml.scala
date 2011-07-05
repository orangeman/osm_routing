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
import Reach.Edge


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


def path(min: Int, routes: List[(Int, Int, List[Int])]) = {

val s = latlon(routes.head._1)
val t = latlon(routes.head._2)
val vias = routes.tail

<kml xmlns="http://www.opengis.net/kml/2.2">
  <Document>
   <Style id="green">
      <IconStyle><color>ff00ff00</color><scale>2</scale></IconStyle>
      <LineStyle><color>ff00ff00</color><width>77</width></LineStyle>
   </Style>
   <Style id="yellow">
      <IconStyle><color>7700ffff</color><scale>1</scale></IconStyle>
      <LineStyle><color>8800ffff</color><width>3</width></LineStyle>
   </Style>
   <Style id="red">
      <IconStyle><color>770000ff</color><scale>0.5</scale></IconStyle>
      <LineStyle><color>440000ff</color><width>2</width></LineStyle>
   </Style>
    <Placemark>
      <name>START</name>
      <description>Distance={min}</description>
      <styleUrl>#green</styleUrl>
      <Point><coordinates> { s.lon },{ s.lat } </coordinates></Point>
    </Placemark>
    <Placemark>
      <name>ZIEL</name>
      <description>Distance={min}</description>
      <styleUrl>#green</styleUrl>
      <Point><coordinates> { t.lon },{ t.lat } </coordinates></Point>
    </Placemark>
  { for ((via, det, path) <- vias) yield
    <Placemark>
      <name>{det}</name>
      <description>foo bar</description>
      { if (det < 1) 
        <styleUrl>#green</styleUrl>
        <MultiGeometry>
         { for ((a,b) <- path zip path.tail) yield
            <LineString><extrude>1</extrude><tessellate>1</tessellate>
              <coordinates> 
                { latlon(a).lon },{ latlon(a).lat },0, { latlon(b).lon },{ latlon(b).lat },0
              </coordinates>
            </LineString>
         }
        </MultiGeometry>
      else if (det < 1500) 
        <styleUrl>#yellow</styleUrl>
        <MultiGeometry>
          <Point>
            <coordinates> { latlon(via).lon },{ latlon(via).lat } </coordinates>
          </Point>
         { for ((a,b) <- path zip path.tail) yield
            <LineString><extrude>1</extrude><tessellate>1</tessellate>
              <coordinates> 
                { latlon(a).lon },{ latlon(a).lat },0, { latlon(b).lon },{ latlon(b).lat },0
              </coordinates>
            </LineString>
         }
        </MultiGeometry>
      else 
        <styleUrl>#red</styleUrl>
        <MultiGeometry>
          <Point>
            <coordinates> { latlon(via).lon },{ latlon(via).lat } </coordinates>
          </Point>
         { for ((a,b) <- path zip path.tail) yield
            <LineString><extrude>1</extrude><tessellate>1</tessellate>
              <coordinates> 
                { latlon(a).lon },{ latlon(a).lat },0, { latlon(b).lon },{ latlon(b).lat },0
              </coordinates>
            </LineString>
         }
        </MultiGeometry>
      }
    </Placemark>
  }     
  </Document>
</kml>

//XML.save("routes.kml", kml, "UTF-8", true, null)

}


def reach() {
  val G = Graph
  val kml =
<kml xmlns="http://www.opengis.net/kml/2.2">
  <Document>
    <Style id="blueLine">
      <LineStyle><color>59ffa500</color><width>55</width></LineStyle></Style>
      <IconStyle><color>ff0000ff</color><scale>0.5</scale></IconStyle>
    <Style id="translucent">
      <LineStyle><color>00000000</color><width>0</width></LineStyle></Style>
      <IconStyle><color>ff0000ff</color><scale>0.5</scale></IconStyle>
		<StyleMap id="style">
      <Pair><key>highlight</key><styleUrl>#blueLine</styleUrl></Pair>
      <Pair><key>normal</key><styleUrl>#translucent</styleUrl></Pair>
    </StyleMap> 
  { for (i <- 0 until G.node_array.length-1) yield
    <Placemark>
      <styleUrl>#style</styleUrl>
      <name>{Reach.get(i)}</name>
      <description>Node Id: {i}</description>
      <MultiGeometry>
        <Point>
          <coordinates> { latlon(i).lon },{ latlon(i).lat } </coordinates>
        </Point>
        { for (j <- G.node_array(i) until G.node_array(i+1)) yield
          <LineString><extrude>1</extrude><tessellate>1</tessellate>
            <coordinates> 
              { latlon(i).lon },{ latlon(i).lat },0,
              { latlon(G.edge_array(j)).lon },{ latlon(G.edge_array(j)).lat },0
            </coordinates>
          </LineString>
        }
      </MultiGeometry>
    </Placemark>
  }     
  </Document>
</kml>

XML.save("reaches.kml", kml, "UTF-8", true, null)

}
}

