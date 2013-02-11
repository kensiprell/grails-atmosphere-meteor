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

import static org.atmosphere.cpr.AtmosphereResource.TRANSPORT.LONG_POLLING
import static org.atmosphere.cpr.AtmosphereResource.TRANSPORT.WEBSOCKET
import grails.converters.JSON

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.atmosphere.cpr.AtmosphereResource
import org.atmosphere.cpr.AtmosphereResourceEventListenerAdapter
import org.atmosphere.cpr.Broadcaster
import org.atmosphere.cpr.BroadcasterFactory
import org.atmosphere.cpr.Meteor
import org.atmosphere.util.SimpleBroadcaster
import org.atmosphere.websocket.WebSocketEventListenerAdapter
import org.json.simple.JSONObject
import org.springframework.context.ApplicationContext

class DefaultMeteorHandler extends HttpServlet {

	ApplicationContext applicationContext = ApplicationContextHolder.applicationContext
	def jabberTestService = applicationContext.getBean("atmosphereTestService")

	@Override
	void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String mapping = request.getHeader("mapping")

		Meteor m = Meteor.build(request)
		if (m.transport().equals(WEBSOCKET)) {
			m.addListener(new WebSocketEventListenerAdapter())
		} else {
			m.addListener(new AtmosphereResourceEventListenerAdapter())
		}

		response.setContentType("application/javascript charset=UTF-8")

		Broadcaster b = BroadcasterFactory.getDefault().lookup(SimpleBroadcaster.class, mapping, true)
		//m.setBroadcaster(b)
		AtmosphereResource resource = m.getAtmosphereResource()
		b.addAtmosphereResource(resource)
		m.resumeOnBroadcast(m.transport() == LONG_POLLING).suspend(-1)
	}

	@Override
	void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		def data = JSON.parse(request.getReader().readLine()) as JSONObject
		String mapping = request.getHeader("mapping")
		String type = data.containsKey("type") ? data.type.toString() : null
		String resource = data.containsKey("resource") ? data.resource.toString() : null
		String message = data.containsKey("message") ? data.message.toString() : null

		if (type == null || resource == null || message == null) {
			// TODO log incomplete message from client
		} else {
			if (message.toLowerCase().contains("<script")) {
				// TODO warn and log potential malicious use
			} else {
				Broadcaster b = BroadcasterFactory.getDefault().lookup(mapping)
				b.broadcast(data)
				jabberTestService.recordChat(data)
			}
		}
	}
}
