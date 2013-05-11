import org.grails.plugins.atmosphere_meteor.DefaultMeteorHandler
import org.grails.plugins.atmosphere_meteor.SimpleMeteorHandler

defaultMapping = "/jabber/*"

servlets = [
	MeteorServlet: [
		className: "org.grails.plugins.atmosphere_meteor.DefaultMeteorServlet",
		mapping: "/jabber/*",
		handler: SimpleMeteorHandler,
		initParams: []
	],
	MeteorServletChat: [
		className: "org.grails.plugins.atmosphere_meteor.DefaultMeteorServlet",
		mapping: "/jabber/chat/*",
		handler: DefaultMeteorHandler
	],
	MeteorServletNotification: [
		className: "org.grails.plugins.atmosphere_meteor.DefaultMeteorServlet",
		mapping: "/jabber/notification/*",
		handler: SimpleMeteorHandler,
		initParams: []
	],
	MeteorServletPublic: [
		className: "org.grails.plugins.atmosphere_meteor.DefaultMeteorServlet",
		mapping: "/jabber/public/*",
		handler: DefaultMeteorHandler
	]
]

defaultInitParams = [
		"org.atmosphere.cpr.CometSupport.maxInactiveActivity": "30000",
		"org.atmosphere.cpr.broadcaster.shareableThreadPool": "true",
		"org.atmosphere.cpr.broadcasterLifeCyclePolicy": "EMPTY_DESTROY",
		"org.atmosphere.cpr.broadcasterCacheClass": org.atmosphere.cache.UUIDBroadcasterCache.UUIDBroadcasterCache
]
