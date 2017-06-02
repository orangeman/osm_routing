osm routing
===========

super simple shortestpaths for [openstreetmap](http://openstreetmap.org)

plain [Dijkstra](https://github.com/orangeman/osm_routing/blob/master/src/main/scala/Dijkstra.scala) implementation in scala with a rest-ful [WebAPI](https://github.com/orangeman/osm_routing/blob/master/src/main/scala/WebAPI.scala )


GET STARTED
-----------
pre-requisites: [git](http://git-scm.com), [sbt](https://github.com/harrah/xsbt)

	$ git clone git://github.com/orangeman/osm_routing.git && cd osm_routing
	$ sbt 
	> update
	> parse osm
	> jetty-run


<br/><br/>

----------------
released under the **GPLv3 license**.


