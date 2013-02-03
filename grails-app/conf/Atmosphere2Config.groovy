/*
 * Copyright (c) 2013. the original author or authors:
 *
 *    Ken Siprell (ken.siprell@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.grails.plugins.atmosphere2.DefaultMeteorHandler

defaultUrl = "/jabber/*"

servlets = [
	MeteorServlet: [
		description: "MeteorServlet Default",
		className: "org.grails.plugins.atmosphere2.DefaultMeteorServlet",
		urlPattern: "/jabber/*",
		handler: DefaultMeteorHandler
	],
	MeteorServletChat: [
		description: "MeteorServlet Chat",
		className: "org.grails.plugins.atmosphere2.DefaultMeteorServlet",
		urlPattern: "/jabber/chat/*",
		handler: DefaultMeteorHandler
	],
	MeteorServletNotification: [
		description: "MeteorServlet Notification",
		className: "org.grails.plugins.atmosphere2.DefaultMeteorServlet",
		urlPattern: "/jabber/notification/*",
		handler: DefaultMeteorHandler
	],
	MeteorServletPublic: [
		description: "MeteorServlet Public",
		className: "org.grails.plugins.atmosphere2.DefaultMeteorServlet",
		urlPattern: "/jabber/public/*",
		handler: DefaultMeteorHandler
	]
]

initParams = [
	"org.atmosphere.cpr.sessionSupport": "true",
	"org.atmosphere.cpr.CometSupport.maxInactiveActivity": "30000",
	"org.atmosphere.cpr.broadcaster.shareableThreadPool": "true",
	"org.atmosphere.cpr.broadcasterLifeCyclePolicy": "IDLE_DESTROY"
]
