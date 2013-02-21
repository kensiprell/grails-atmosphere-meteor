import javax.servlet.ServletRegistration

import org.grails.plugins.atmosphere_meteor.ApplicationContextHolder
import org.grails.plugins.atmosphere_meteor.MeteorHandlerArtefactHandler
import org.grails.plugins.atmosphere_meteor.MeteorServletArtefactHandler

class AtmosphereMeteorGrailsPlugin {
	def version = "0.4.1"
	def grailsVersion = "2.1 > *"
	def pluginExcludes = [
			"**/atmosphere/**",
			"**/atmosphereTest/**",
			"grails-app/conf/AtmosphereMeteorConfig.groovy",
			"grails-app/controllers/org/grails/plugins/atmosphere_meteor/AtmosphereTestController.groovy",
			"grails-app/services/org/grails/plugins/atmosphere_meteor/AtmosphereTestService.groovy",
			"web-app/css/**",
			"web-app/images/**",
			"web-app/js/application.js"
	]

	def title = "Atmosphere Meteor Plugin"
	def author = "Ken Siprell"
	def authorEmail = "ken.siprell@gmail.com"
	def description = '''
This plugin incorporates the Atmosphere Framework (https://github.com/Atmosphere/atmosphere/wiki). It can form the basis for a traditional XMPP server with a browser-based client without the limitations of BOSH. You can also download the plugin source code and run it as a standalone application and take the plugin for a test drive before installing.
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
			//"file:../../plugins/*/atmosphere/**/*MeteorHandler.groovy",
			//"file:../../plugins/*/atmosphere/**/*MeteorServlet.groovy"
	]

	def onChange = { event ->
		/*
		event.source - The source of the event, either the reloaded Class or a Spring Resource
		event.ctx - The Spring ApplicationContext instance
		event.plugin - The plugin object that manages the resource (usually this)
		event.application - The GrailsApplication instance
		event.manager - The GrailsPluginManager instance
		*/

		// Change in AtmosphereMeteorConfig.groovy
		if (event.source.name == "AtmosphereMeteorConfig") {
			println "\nChanges to AtmosphereMeteorConfig.groovy will not be implemented until the application is restarted.\n"
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
		appContext = applicationContext
		def config = ApplicationContextHolder.atmosphereMeteorConfig
		def servletContext = applicationContext.servletContext

		config?.servlets?.each { name, parameters ->
			ServletRegistration servletRegistration = servletContext.addServlet(name, parameters.className)
			servletRegistration.addMapping(parameters.mapping)
			servletRegistration.setLoadOnStartup(1)
			def initParams = parameters.initParams ?: config?.defaultInitParams
			initParams?.each { param, value ->
				servletRegistration.setInitParameter(param, value)
			}
		}
	}
}
