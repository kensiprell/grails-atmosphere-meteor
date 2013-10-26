@artifact.package@

import org.atmosphere.cpr.MeteorServlet
import org.atmosphere.handler.ReflectorServletProcessor

import org.grails.plugins.atmosphere_meteor.ApplicationContextHolder

import javax.servlet.http.HttpServlet
import javax.servlet.ServletConfig
import javax.servlet.ServletException

class @artifact.name@ extends MeteorServlet {

	@Override
	public void init(ServletConfig sc) throws ServletException {
		super.init(sc)

		def servletName = sc.servletName
		def config = ApplicationContextHolder.atmosphereMeteorConfig
		def servlet = config.servlets.get(servletName)
		def mapping = servlet.mapping
		def handler = servlet.handler.newInstance() as HttpServlet
		def servletClass = handler.class.getName()

		ReflectorServletProcessor r = new ReflectorServletProcessor(handler)
		r.setServletClassName(servletClass)
		framework.addAtmosphereHandler(mapping, r).initAtmosphereHandler(sc)
		logger.info "Added AtmosphereHandler: $servletClass mapped to $mapping"
	}
}
