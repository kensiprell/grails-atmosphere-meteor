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



import org.codehaus.groovy.grails.cli.fork.ForkedGrailsProcess
import org.grails.plugins.atmosphere2.ApplicationContextHolder
import org.grails.plugins.atmosphere2.MeteorHandlerArtefactHandler
import org.grails.plugins.atmosphere2.MeteorServletArtefactHandler
import org.springframework.util.ClassUtils

import javax.servlet.ServletRegistration

class Atmosphere2GrailsPlugin {
	// the plugin version
	def version = "0.3.1"
	// the version or versions of Grails the plugin is designed for
	def grailsVersion = "2.1 > *"
	// the other plugins this plugin depends on
	def dependsOn = [:]
	// resources that are excluded from plugin packaging
	def pluginExcludes = [
			"**/atmosphere/**",
			"**/atmosphereTest/**",
			"grails-app/conf/Atmosphere2Config.groovy",
			"grails-app/controllers/org/grails/plugins/atmosphere2/AtmosphereTestController.groovy",
			"grails-app/services/org/grails/plugins/atmosphere2/AtmosphereTestService.groovy",
			"grails-app/views/error.gsp"
	]

	def title = "Atmosphere2 Plugin"
	def author = "Ken Siprell"
	def authorEmail = "ken.siprell@gmail.com"
	def description = '''
This plugin incorporates the Atmosphere Framework (https://github.com/Atmosphere/atmosphere/wiki). It can form the basis for a traditional XMPP server with a browser-based client without the limitations of BOSH.

You can also download the plugin source code and run it as a standalone app and take the plugin for a test drive before installing.
'''

	// URL to the plugin's documentation
	def documentation = "https://github.com/kensiprell/grails-atmosphere2/blob/master/README.md.md"

	// License: one of 'APACHE', 'GPL2', 'GPL3'
	def license = "APACHE"

	// Details of company behind the plugin (if there is one)
	// def organization = [ name: "Example, Inc.", url: "http://www.example.com/" ]

	// Any additional developers beyond the author specified above.
	// def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

	// Location of the plugin's issue tracker.
	def issueManagement = [system: "github", url: "https://github.com/kensiprell/grails-atmosphere2/issues"]

	// Online location of the plugin's browseable source code.
	def scm = [url: "https://github.com/kensiprell/grails-atmosphere2"]

	def appContext

	def artefacts = [MeteorHandlerArtefactHandler, MeteorServletArtefactHandler]

	def watchedResources = [
			"file:./grails-app/atmosphere/**/*MeteorHandler.groovy",
			"file:./grails-app/atmosphere/**/*MeteorServlet.groovy",
			"file:./grails-app/conf/Atmosphere2Config.groovy"
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

/*
		println ""
		println "event.source: ${event.source}"
		println "event.source.name: ${event.source.name}"
		println "event.ctx: ${event.ctx}"
		println "event.plugin: ${event.plugin}"
		println "event.application: ${event.application}"
		println "event.manager: ${event.manager}"
		println ""
*/

		// Change in Atmosphere2Config.groovy
		if (event.source.name == "Atmosphere2Config") {
			println "\nChanges to Atmosphere2Config.groovy will not be implemented until the application is restarted.\n"
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

	def doWithWebDescriptor = { xml ->
/*
		def servlets = xml.'servlet'
		def mappings = xml.'servlet-mapping'
		def config = ApplicationContextHolder.atmosphere2Config

		config?.servlets?.each { name, parameters ->
			servlets[servlets.size() - 1] + {
				'servlet' {
					'servlet-name'(name)
					'servlet-class'(parameters.className)
					if (ClassUtils.isPresent("javax.servlet.AsyncContext", Thread.currentThread().getContextClassLoader())) {
						'async-supported'(true)
					}
					config.initParams.each { initParam ->
						if (initParam.key && initParam.value) {
							'init-param' {
								'param-name'(initParam.key)
								'param-value'(initParam.value)
							}
						}
					}
					'load-on-startup'('0')
				}
			}
			mappings[mappings.size() - 1] + {
				'servlet-mapping' {
					'servlet-name'(name)
					'url-pattern'(parameters.mapping)
				}
			}
		}
*/
	}

	def doWithDynamicMethods = { applicationContext ->
		// Implement registering dynamic methods to classes (optional)
		appContext = applicationContext
		def config = ApplicationContextHolder.atmosphere2Config
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

	def doWithSpring = {
		// Implement runtime spring config (optional)
	}

	def doWithApplicationContext = { applicationContext ->
		// Implement post initialization spring config (optional)
	}

	def onConfigChange = { event ->
		// Implement code that is executed when the project configuration changes.
		// The event is the same as for 'onChange'.
	}

	def onShutdown = { event ->
		// Implement code that is executed when the application shuts down (optional)
	}
}
