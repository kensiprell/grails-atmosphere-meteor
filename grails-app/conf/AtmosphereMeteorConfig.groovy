import org.grails.plugins.atmosphere_meteor.DefaultMeteorHandler
import org.grails.plugins.atmosphere_meteor.SimpleMeteorHandler

defaultMapping = "/jabber/*"

servlets = [
	MeteorServlet: [
		className: "org.grails.plugins.atmosphere_meteor.DefaultMeteorServlet",
		mapping: "/jabber/*",
		handler: DefaultMeteorHandler
	],
	MeteorServletChat: [
		className: "org.grails.plugins.atmosphere_meteor.DefaultMeteorServlet",
		mapping: "/jabber/chat/*",
		handler: DefaultMeteorHandler
	],
	MeteorServletNotification: [
		className: "org.grails.plugins.atmosphere_meteor.DefaultMeteorServlet",
		mapping: "/jabber/notification/*",
		handler: DefaultMeteorHandler
	],
	MeteorServletPublic: [
		className: "org.grails.plugins.atmosphere_meteor.DefaultMeteorServlet",
		mapping: "/jabber/public/*",
		handler: SimpleMeteorHandler,
		initParams: [
			"org.atmosphere.cpr.broadcaster.shareableThreadPool": "true",
		]
	]
]

defaultInitParams = [
	"org.atmosphere.cpr.CometSupport.maxInactiveActivity": "30000",
	"org.atmosphere.cpr.broadcaster.shareableThreadPool": "true",
	"org.atmosphere.cpr.broadcasterLifeCyclePolicy": "EMPTY_DESTROY"
]
