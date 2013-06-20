@artifact.package@

//import org.atmosphere.cpr.DefaultBroadcaster
import org.atmosphere.config.service.MeteorService
import org.atmosphere.util.SimpleBroadcaster

import static org.atmosphere.cpr.AtmosphereResource.TRANSPORT.LONG_POLLING
import static org.atmosphere.cpr.AtmosphereResource.TRANSPORT.WEBSOCKET

import grails.converters.JSON

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.atmosphere.cpr.AtmosphereResourceEventListenerAdapter
import org.atmosphere.cpr.Broadcaster
import org.atmosphere.cpr.BroadcasterFactory
import org.atmosphere.cpr.Meteor
import org.atmosphere.websocket.WebSocketEventListenerAdapter
import org.json.simple.JSONObject
import org.springframework.context.ApplicationContext
import org.grails.plugins.atmosphere_meteor.ApplicationContextHolder

@MeteorService
class @artifact.name@ extends HttpServlet {

	ApplicationContext applicationContext = ApplicationContextHolder.applicationContext

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String mapping = request.getHeader("AtmosphereMeteor-Mapping")

		Meteor m = Meteor.build(request)
		if (m.transport().equals(WEBSOCKET)) {
			m.addListener(new WebSocketEventListenerAdapter())
		} else {
			m.addListener(new AtmosphereResourceEventListenerAdapter())
		}

		response.setContentType("text/html;charset=UTF-8")

		//Broadcaster b = BroadcasterFactory.getDefault().lookup(DefaultBroadcaster.class, mapping, true)
		Broadcaster b = BroadcasterFactory.getDefault().lookup(SimpleBroadcaster.class, mapping, true)
		m.setBroadcaster(b)
		m.resumeOnBroadcast(m.transport() == LONG_POLLING).suspend(-1)
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		def data = JSON.parse(request.getReader().readLine()) as JSONObject
		String mapping = request.getHeader("AtmosphereMeteor-Mapping")
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
				// TODO record chat message
				// chatService.recordChat(data)
			}
		}
	}
}
