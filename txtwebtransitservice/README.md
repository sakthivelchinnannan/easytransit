####EasyTransit txtWeb Service V1####

This service uses Google Directions API with *transit* travel mode.

You can get Bus and Train details to commute within a city in India for current time or for a future departure (to start from SOURCE) or arriving (to reach DEST) time by sending a SMS to txtWeb.

#####Todo:#####
1.	Have to display time of departure for Buses.
2.	Have to include other vehicle types like `'METRO_RAIL'`, etc,. which are common in other cities (Currently results are displayed only for `BUS` and `HEAVY_RAIL` as in Chennai).

#####Important:#####
*	Note 1: Use of the Google Directions API is subject to a query limit of 625 transit directions requests per day. So you may get `OVER_QUERY_LIMIT` error.
	Link: [Google Directions API usage limits](https://developers.google.com/maps/documentation/directions/)
*	Note 2: For the list of cities covered by Google Transit in India [Click here](http://www.google.com/intl/en/landing/transit/text.html#as)

#####Warning:#####
Use these directions only for **_planning purposes_**. You may find that construction projects, traffic, weather, or other events may cause conditions to differ from the map results, and you should plan your route accordingly.

####How to use this Service####

`@easytransit SOURCE, DEST[, time]`
Time format: `(d/a):d-m-yy h.mm(am/pm)`

#####Eg:#####
*	To get the public transit details for current time:
   
	`@easytransit Guindy Chennai, Chrompet Chennai`

*	To get public transit details at a particular time in future:
   
	`@easytransit Tambaram, Guindy, d:20-9-12 9.00am`
	  
	(Here the *'d:'* in time specifies that i wanted details for starting from Tambaram at 9.00am)

	`@easytransit Tambaram, Guindy, a:20-9-12 10.00am`
	  
	(Here the *'a:'* in time specifies that i wanted details for reaching Guindy at 10.00am)

####txtWeb Service Home Page####
[Easytransit txtWeb Service](http://developer.txtweb.com/user/apps/easytransit)