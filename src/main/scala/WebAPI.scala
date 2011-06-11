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


class WebApi extends ScalatraFilter with ScalateSupport {


  get("/*.kml") {

	val start = System.currentTimeMillis()
	if (!params.contains("from") || !params.contains("to"))
		<h1>please specify origin and destination ids</h1>
	else {	
		val path = Dijkstra.shortestPath(params("from").toInt, params("to").toInt)
		println("FOUND ROUTE ("+(System.currentTimeMillis()-start)+"ms)")
		contentType = "application/vnd.google-earth.kml+xml"
		kml.build(path)	
	}
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

