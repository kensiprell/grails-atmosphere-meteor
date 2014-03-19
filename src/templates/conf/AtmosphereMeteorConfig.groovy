/*
 Import your handler classes

import your.package.ChatMeteorHandler
import your.package.ForumMeteorHandler
import your.package.SimpleMeteorHandler
*/

/*
 defaultMapping is used by _Events.groovy to create atmosphere-meteor-decorators.xml
 and update sitemesh.xml in web-app/WEB-INF.

*/
defaultMapping = "/atmosphere/*"

/*
 The defaultInitParams below are added to each MeteorServlet
 unless the servlet has specified its own initParams map.
 See http:pastehtml.com/view/cgwfei5nu.html for details.
*/

defaultInitParams = [
		// Uncomment the line below use native WebSocket support with native Comet support.
		//"org.atmosphere.useWebSocketAndServlet3": "false",
		"org.atmosphere.cpr.broadcasterCacheClass": "org.atmosphere.cache.UUIDBroadcasterCache",
		"org.atmosphere.cpr.AtmosphereInterceptor": """
			org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor,
			org.atmosphere.interceptor.HeartbeatInterceptor
		"""
]

/*
 name (index), className, and mapping are used by
 AtmosphereMeteorGrailsPlugin.doWithWebDescriptor to create the servlets in web.xml.

 mapping and handler are used by the DefaultMeteorServlet class
 to add each AtmosphereHandler to the AtmosphereFramework.

 Uncomment and edit the example below to configure your servlets
*/

/*
servlets = [
	MeteorServlet: [
		className: "your.package.DefaultMeteorServlet",
		mapping: "/atmosphere/*",
		handler: SimpleMeteorHandler,
		initParams = [
			// Uncomment the line below use native WebSocket support with native Comet support.
			//"org.atmosphere.useWebSocketAndServlet3": "false",
			"org.atmosphere.cpr.broadcasterCacheClass": "org.atmosphere.cache.UUIDBroadcasterCache",
			"org.atmosphere.cpr.AtmosphereInterceptor": """
				org.atmosphere.client.TrackMessageSizeInterceptor,
				org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor,
				org.atmosphere.interceptor.HeartbeatInterceptor
		]
	],
	MeteorServletChat: [
		className: "your.package.DefaultMeteorServlet",
		mapping: "/atmosphere/chat/*",
		handler: ChatMeteorHandler,
		initParams: defaultInitParams
	]
	MeteorServletForum: [
		className: "your.package.DefaultMeteorServlet",
		mapping: "/atmosphere/forum/*",
		handler: ForumMeteorHandler,
		initParams: "none"
	]
]
*/

