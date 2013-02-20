package org.grails.plugins.atmosphere_meteor

import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet

import org.atmosphere.cpr.MeteorServlet
import org.atmosphere.handler.ReflectorServletProcessor

class DefaultMeteorServlet extends MeteorServlet {

	@Override
	void init(ServletConfig sc) throws ServletException {
		super.init(sc)

		HttpServlet handler
		String mapping
		String servletClass
		String servletName = sc.servletName
		def config = ApplicationContextHolder.atmosphereMeteorConfig
		def servlet = config.servlets.get(servletName)

		handler = servlet.handler.newInstance()
		mapping = servlet.mapping
		servletClass = handler.class.getName()
		ReflectorServletProcessor r = new ReflectorServletProcessor(handler)
		r.setServletClassName(servletClass)
		framework.addAtmosphereHandler(mapping, r).initAtmosphereHandler(sc)
		logger.info("Installed DefaultMeteorServlet ${servletClass} mapped to ${mapping}")
	}
}
