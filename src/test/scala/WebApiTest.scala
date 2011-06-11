package de.andlabs.routing

import org.scalatra._
import org.scalatra.test.scalatest._
import org.scalatest.matchers._

class WebApiTestSuite extends ScalatraFunSuite with ShouldMatchers {

  addFilter(classOf[WebApi], "/*")

  test("GET / returns status 200") {
    get("/") { 
      status should equal (200)
    }
  }

  test("GET /something.kml") {
    get("/something.kml") { 
      status should equal (200)
      println(body)
      body should equal ("<h1>please specify origin and destination ids</h1>")
    }
  }

  test("GET /something.kml with params") {
    get("/something.kml?from=23&to=224") { 
      status should equal (200)
      body should startWith ("<kml")
      body should include ("<name>225</name>")
      body should include ("<name>221</name>")
    }
  }


}
