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



package org.grails.plugins.atmosphere2

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
