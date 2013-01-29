@artifact.package@
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

import grails.util.BuildSettingsHolder
import javax.servlet.http.HttpServlet
import javax.servlet.ServletConfig
import javax.servlet.ServletException
import org.atmosphere.cpr.MeteorServlet
import org.atmosphere.handler.ReflectorServletProcessor

class @artifact.name@ extends MeteorServlet {

	@Override
	public void init(ServletConfig sc) throws ServletException {
		super.init(sc)

		HttpServlet handler
		String mapping
		String servletClass
		String servletName = sc.servletName
		def basedir = BuildSettingsHolder.settings.baseDir
		def config = new ConfigSlurper().parse(new File("${basedir}/grails-app/conf/Atmosphere2Config.groovy").toURI().toURL())
		def servlet = config.servlets.get(servletName)

		handler = servlet.handler.newInstance() as HttpServlet
		mapping = servlet.urlPattern
		servletClass = handler.class.getName()
		ReflectorServletProcessor r = new ReflectorServletProcessor(handler)
		r.setServletClassName(servletClass)
		framework.addAtmosphereHandler(mapping, r).initAtmosphereHandler(sc)
		logger.info("Installed MeteorServlet ${servletClass} mapped to ${mapping}")
	}
}
