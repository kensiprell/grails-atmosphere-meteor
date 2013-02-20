package org.grails.plugins.atmosphere_meteor

import grails.converters.JSON
import org.atmosphere.cpr.Broadcaster
import org.atmosphere.cpr.BroadcasterFactory

class AtmosphereTestController {

	def index() {
		render(view: "index")
	}

	def triggerPublic() {
		Thread.start {
			for (int i = 0; i < 100; i++) {
				def response = publicResponse()
				Broadcaster b = BroadcasterFactory.getDefault().lookup("/jabber/public", true)
				b.broadcast(response)
				sleep(5000)
			}
		}
		render "success"
	}

	def publicResponse() {
		return [type: "public", resource: "/jabber/public", message: new Date()] as JSON
	}

}
