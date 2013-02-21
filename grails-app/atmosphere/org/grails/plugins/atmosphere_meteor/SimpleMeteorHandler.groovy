package org.grails.plugins.atmosphere_meteor

import static org.atmosphere.cpr.AtmosphereResource.TRANSPORT.LONG_POLLING
import static org.atmosphere.cpr.AtmosphereResource.TRANSPORT.WEBSOCKET

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.atmosphere.cpr.AtmosphereResourceEventListenerAdapter
import org.atmosphere.cpr.Broadcaster
import org.atmosphere.cpr.BroadcasterFactory
import org.atmosphere.cpr.Meteor
import org.atmosphere.util.SimpleBroadcaster
import org.atmosphere.websocket.WebSocketEventListenerAdapter
import org.springframework.context.ApplicationContext

class SimpleMeteorHandler extends HttpServlet {

	ApplicationContext applicationContext = ApplicationContextHolder.applicationContext

	@Override
	void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String mapping = request.getHeader("AtmosphereMeteor-Mapping")

		Meteor m = Meteor.build(request)
		if (m.transport().equals(WEBSOCKET)) {
			m.addListener(new WebSocketEventListenerAdapter())
		} else {
			m.addListener(new AtmosphereResourceEventListenerAdapter())
		}

		response.setContentType("text/html;charset=UTF-8")

		Broadcaster b = BroadcasterFactory.getDefault().lookup(SimpleBroadcaster.class, mapping, true)
		m.setBroadcaster(b)
		m.resumeOnBroadcast(m.transport() == LONG_POLLING).suspend(-1)
	}

	@Override
	void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
	}
}
