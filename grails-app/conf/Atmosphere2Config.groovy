import org.grails.plugins.atmosphere2.DefaultMeteorHandler

defaultMapping = "/jabber/*"

servlets = [
	MeteorServlet: [
		className: "org.grails.plugins.atmosphere2.DefaultMeteorServlet",
		mapping: "/jabber/*",
		handler: DefaultMeteorHandler,
		initParams: [
			"org.atmosphere.cpr.sessionSupport": "true",
			"org.atmosphere.cpr.CometSupport.maxInactiveActivity": "30000",
			"org.atmosphere.cpr.broadcaster.shareableThreadPool": "true",
			"org.atmosphere.cpr.broadcasterLifeCyclePolicy": "IDLE_DESTROY"
		]
	],
	MeteorServletChat: [
		className: "org.grails.plugins.atmosphere2.DefaultMeteorServlet",
		mapping: "/jabber/chat/*",
		handler: DefaultMeteorHandler
	],
	MeteorServletNotification: [
		className: "org.grails.plugins.atmosphere2.DefaultMeteorServlet",
		mapping: "/jabber/notification/*",
		handler: DefaultMeteorHandler
	],
	MeteorServletPublic: [
		className: "org.grails.plugins.atmosphere2.DefaultMeteorServlet",
		mapping: "/jabber/public/*",
		handler: DefaultMeteorHandler
	]
]

defaultInitParams = [
	"org.atmosphere.cpr.sessionSupport": "true",
	"org.atmosphere.cpr.CometSupport.maxInactiveActivity": "30000",
	"org.atmosphere.cpr.broadcaster.shareableThreadPool": "true",
	"org.atmosphere.cpr.broadcasterLifeCyclePolicy": "IDLE_DESTROY"
]
