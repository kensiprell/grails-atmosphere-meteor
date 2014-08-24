@artifact.package@

import grails.converters.JSON
import grails.util.Holders

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.atmosphere.cpr.AtmosphereResourceEventListenerAdapter
import org.atmosphere.cpr.Broadcaster
import org.atmosphere.cpr.DefaultBroadcaster
import org.atmosphere.cpr.Meteor

class @artifact.name@ extends HttpServlet {
	def atmosphereMeteor = Holders.applicationContext.getBean("atmosphereMeteor")

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String mapping
		// TODO define your mapping
		//mapping = "/atmosphere" + request.getPathInfo
		//mapping = URLDecoder.decode(request.getHeader("X-AtmosphereMeteor-Mapping"), "UTF-8")

		Meteor meteor = Meteor.build(request)
		Broadcaster broadcaster = atmosphereMeteor.broadcasterFactory.lookup(DefaultBroadcaster.class, mapping, true)

		meteor.addListener(new AtmosphereResourceEventListenerAdapter())
		meteor.setBroadcaster(broadcaster)
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String mapping
		// TODO define your mapping
		//mapping = "/atmosphere" + request.getPathInfo
		//mapping = URLDecoder.decode(request.getHeader("X-AtmosphereMeteor-Mapping"), "UTF-8")

		def jsonMap = JSON.parse(request.getReader().readLine().trim()) as Map

		Broadcaster broadcaster = atmosphereMeteor.broadcasterFactory.lookup(DefaultBroadcaster.class, mapping)
		broadcaster.broadcast(jsonMap)
	}
}
