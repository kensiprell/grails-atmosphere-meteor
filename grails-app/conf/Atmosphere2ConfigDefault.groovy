/*
 Edit line below to import your handler classes
 */
//import your.package.DefaultMeteorHandler

/*
 defaultUrl is used by _Events.groovy to create
 atmosphere2-decorators.xml and update sitemesh.xml
 in web-app/WEB-INF.
 */
defaultUrl = "/jabber/*"

/*
 name (index), description, className, and urlPattern are used by
 Atmosphere2GrailsPlugin.doWithWebDescriptor to create the servlets in web.xml.

 urlPattern and handler are used by the DefaultMeteorServlet class
 to add each AtmosphereHandler to the AtmosphereFramework.

 Uncomment and edit the example below to configure your servlets
 */

//servlets = [
//	MeteorServlet: [
//		description: "MeteorServlet Default",
//		className: "your.package.DefaultMeteorServlet",
//		urlPattern: "/jabber*/",
//		handler: DefaultMeteorHandler
//	],
//	MeteorServletChat: [
//		description: "MeteorServlet Chat",
//		className: "your.package.DefaultMeteorServlet",
//		urlPattern: "/jabber/chat*/",
//		handler: DefaultMeteorHandler
//	]
//]

/*
 The initParams are added to each MeteorServlet created above.
 See http://pastehtml.com/view/cgwfei5nu.html for details.

 Uncomment and edit the example below to configure your servlets
 */

//initParams = [
//	"org.atmosphere.cpr.sessionSupport": "true",
//	"org.atmosphere.cpr.CometSupport.maxInactiveActivity": "30000",
//	"org.atmosphere.cpr.broadcaster.shareableThreadPool": "true",
//	"org.atmosphere.cpr.broadcasterLifeCyclePolicy": "IDLE_DESTROY"
//]

