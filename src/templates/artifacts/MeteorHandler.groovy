@artifact.package@

import org.atmosphere.cpr.AtmosphereResourceEventListenerAdapter
import org.atmosphere.cpr.Broadcaster
import org.atmosphere.cpr.BroadcasterFactory
import org.atmosphere.cpr.DefaultBroadcaster
import org.atmosphere.cpr.Meteor

import grails.converters.JSON

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import grails.util.Holders

class @artifact.name@ extends HttpServlet {

	// TODO inject one of your services
	//def mySuperDuperService = Holders.applicationContext.getBean("mySuperDuperService")

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String mapping
		// TODO define your mapping
		//mapping = "/atmosphere" + request.getPathInfo
		//mapping = URLDecoder.decode(request.getHeader("X-AtmosphereMeteor-Mapping"), "UTF-8")

		Meteor m = Meteor.build(request)
		Broadcaster b = BroadcasterFactory.getDefault().lookup(DefaultBroadcaster.class, mapping, true)

		m.addListener(new AtmosphereResourceEventListenerAdapter())
		m.setBroadcaster(b)
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String mapping
		// TODO define your mapping
		//mapping = "/atmosphere" + request.getPathInfo
		//mapping = URLDecoder.decode(request.getHeader("X-AtmosphereMeteor-Mapping"), "UTF-8")

		def jsonMap = JSON.parse(request.getReader().readLine().trim()) as Map

		Broadcaster b = BroadcasterFactory.getDefault().lookup(DefaultBroadcaster.class, mapping)
		b.broadcast(jsonMap)
	}
}
