import javax.servlet.ServletRegistration

import org.grails.plugins.atmosphere_meteor.ApplicationContextHolder
import org.grails.plugins.atmosphere_meteor.MeteorHandlerArtefactHandler
import org.grails.plugins.atmosphere_meteor.MeteorServletArtefactHandler

class AtmosphereMeteorGrailsPlugin {
	// TODO update version here and in README.md
	def version = "0.7.0"
	def grailsVersion = "2.1 > *"
	def pluginExcludes = [
			"web-app/css/**",
			"web-app/images/**",
			"web-app/js/application.js"
	]

	def title = "Atmosphere Meteor Plugin"
	def author = "Ken Siprell"
	def authorEmail = "ken.siprell@gmail.com"

	def description = '''
This plugin incorporates the [Atmosphere Framework|https://github.com/Atmosphere/atmosphere], which includes client and server-side components for building asynchronous web applications.
'''

	def documentation = "https://github.com/kensiprell/grails-atmosphere-meteor/blob/master/README.md"

	def license = "APACHE"
	def issueManagement = [system: "github", url: "https://github.com/kensiprell/grails-atmosphere-meteor/issues"]
	def scm = [url: "https://github.com/kensiprell/grails-atmosphere-meteor"]

	def appContext

	def artefacts = [MeteorHandlerArtefactHandler, MeteorServletArtefactHandler]

	def watchedResources = [
			"file:./grails-app/atmosphere/**/*MeteorHandler.groovy",
			"file:./grails-app/atmosphere/**/*MeteorServlet.groovy",
			"file:./grails-app/conf/AtmosphereMeteorConfig.groovy"
	]

	def onChange = { event ->
		// Change in AtmosphereMeteorConfig.groovy
		if (event.source.name == "AtmosphereMeteorConfig") {
			println "\nChanges to AtmosphereMeteorConfig.groovy will be implemented when the application is restarted.\n"
			/*
			application.meteorServletClasses.each {
				def newClass = application.classLoader.loadClass(it.clazz.name)
				application.addArtefact(MeteorServletArtefactHandler.TYPE, newClass)
			}
			*/
		}

		// Change in a MeteorHandler
		if (application.isArtefactOfType(MeteorHandlerArtefactHandler.TYPE, event.source.name)) {
			def oldClass = application.getMeteorHandlerClass(event.source.name)
			application.addArtefact(MeteorHandlerArtefactHandler.TYPE, event.source)
			application.meteorHandlerClasses.each {
				if (it.clazz != event.source && oldClass.clazz.isAssignableFrom(it.clazz)) {
					def newClass = application.classLoader.reloadClass(it.clazz.name)
					application.addArtefact(MeteorHandlerArtefactHandler.TYPE, newClass)
				}
			}
		}

		// Change in a MeteorServlet
		if (application.isArtefactOfType(MeteorServletArtefactHandler.TYPE, event.source.name)) {
			def oldClass = application.getMeteorServletClass(event.source.name)
			application.addArtefact(MeteorServletArtefactHandler.TYPE, event.source)
			application.meteorServletClasses.each {
				if (it.clazz != event.source && oldClass.clazz.isAssignableFrom(it.clazz)) {
					def newClass = application.classLoader.reloadClass(it.clazz.name)
					application.addArtefact(MeteorServletArtefactHandler.TYPE, newClass)
				}
			}
		}
	}

	def doWithDynamicMethods = { applicationContext ->
		// Configure servlets
		def config = ApplicationContextHolder.atmosphereMeteorConfig
		def servletContext =  applicationContext.servletContext
		def serverInfo = servletContext.getServerInfo()
		boolean jetty = serverInfo.contains("jetty")
		boolean tomcat = serverInfo.contains("Tomcat")

		//println "\n\ngetServerInfo: $serverInfo\n\n"
		if (jetty) {
			def m = serverInfo =~ /jetty\/(.*)/
			def jettyVersion =  m[0][1]
			if (jettyVersion.getAt(0) < 8.toString()) {
				def versionLine = "* It appears you are using version $jettyVersion.".padRight(67, " ")
				println """
********************************************************************
* The atmosphere-meteor plugin requires at least Jetty version 8.  *
*                                                                  *
$versionLine*
*                                                                  *
* Jetty documentation:                                             *
* https://github.com/kensiprell/grails-atmosphere-meteor#jetty     *
********************************************************************
"""
			}
		}

		config?.servlets?.each { name, parameters ->
			ServletRegistration servletRegistration = servletContext.addServlet(name, parameters.className)
			servletRegistration.addMapping(parameters.mapping)
			servletRegistration.setAsyncSupported(Boolean.TRUE)
			servletRegistration.setLoadOnStartup(1)
			if (jetty) {
				servletRegistration.setInitParameter("org.atmosphere.cpr.asyncSupport", "org.atmosphere.container.JettyServlet30AsyncSupportWithWebSocket")
			}
			if (tomcat) {
				servletRegistration.setInitParameter("org.atmosphere.cpr.asyncSupport", "org.atmosphere.container.Tomcat7AsyncSupportWithWebSocket")
			}
			def initParams = parameters.initParams
			if (initParams != "none") {
				initParams?.each { param, value ->
					servletRegistration.setInitParameter(param, value)
				}
			}
		}
	}

	def doWithSpring = {
		// Register ApplicationContextHolder bean
		applicationContextHolder(ApplicationContextHolder) { bean ->
			bean.factoryMethod = 'getInstance'
		}
	}
}
