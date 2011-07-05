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

import org.scalatra._
import java.net.URL
import scalate.ScalateSupport
import de.andlabs.routing.Graph.Node


class WebApi extends ScalatraFilter with ScalateSupport {


  Graph.load("r_") // when starting server


  get("/route.kml") {
      val start = System.currentTimeMillis()
      val path = new Dijkstra(params("from").toInt, params("to").toInt).getPath
      println((System.currentTimeMillis()-start)+"ms  ("+path.size+" nodes)\n")
      contentType = "application/vnd.google-earth.kml+xml"
      kml.build(path)	
    //}
  }

  get("/routes.kml") {
      val start = System.currentTimeMillis()
      val (dist, path) = new ReachDijkstra(params("from").toInt, params("to").toInt).run
      println((System.currentTimeMillis()-start)+"ms  ("+path.size+" nodes)\n")
      contentType = "application/vnd.google-earth.kml+xml"
      kml.path(dist, path)	
    //}
  }

  get("/hello") {
    contentType = "text/html"
    <h2>{MyJavaClass.sayHello}</h2>
  }
















  notFound {
    // If no route matches, then try to render a Scaml template
    val templateBase = requestPath match {
      case s if s.endsWith("/") => s + "index"
      case s => s
    }
    val templatePath = "/WEB-INF/scalate/templates/" + templateBase + ".scaml"
    servletContext.getResource(templatePath) match {
      case url: URL => 
        contentType = "text/html"
        templateEngine.layout(templatePath)
      case _ => 
        filterChain.doFilter(request, response)
    } 
  }



}

