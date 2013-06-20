/*
 Import your handler classes

import your.package.ChatMeteorHandler
import your.package.ForumMeteorHandler
import your.package.SimpleMeteorHandler
*/

/*
 defaultMapping is used by _Events.groovy to create atmosphere-meteor-decorators.xml
 and update sitemesh.xml in web-app/WEB-INF.

defaultMapping = "/jabber/*"
*/

/*
 name (index), description, className, and mapping are used by
 AtmosphereMeteorGrailsPlugin.doWithWebDescriptor to create the servlets in web.xml.

 mapping and handler are used by the DefaultMeteorServlet class
 to add each AtmosphereHandler to the AtmosphereFramework.

 Uncomment and edit the example below to configure your servlets

servlets = [
	MeteorServlet: [
		className: "your.package.DefaultMeteorServlet",
		mapping: "/jabber/*",
		handler: SimpleMeteorHandler,
		initParams = [
			// You can config each servlet to use its own initParams here
			// instead of using the defaultInitParams below.
		]
	],
	MeteorServletChat: [
		className: "your.package.DefaultMeteorServlet",
		mapping: "/jabber/chat/*",
		handler: ChatMeteorHandler
	]
	MeteorServletChat: [
		className: "your.package.DefaultMeteorServlet",
		mapping: "/jabber/forum/*",
		handler: ForumMeteorHandler
	]
]
*/


/*
 The initParams are added to each MeteorServlet created above.
 See http:pastehtml.com/view/cgwfei5nu.html for details.

 Uncomment and edit the example below to configure your servlets.
 These parameters will be applied if a servlet defined above does
 not specify an initParams map.

defaultInitParams = [
		"org.atmosphere.useNative": "true",
		"org.atmosphere.cpr.CometSupport.maxInactiveActivity": "30000",
		"org.atmosphere.cpr.broadcasterLifeCyclePolicy": "EMPTY_DESTROY",
		"org.atmosphere.cpr.broadcasterCacheClass": "org.atmosphere.cache.UUIDBroadcasterCache",
		"org.atmosphere.cpr.AtmosphereInterceptor": """
			org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor,
			org.atmosphere.interceptor.HeartbeatInterceptor,
			org.atmosphere.client.TrackMessageSizeInterceptor
		"""
]
*/

