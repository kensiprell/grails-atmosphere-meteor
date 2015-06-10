import grails.util.BuildSettingsHolder
import grails.util.Environment
import grails.util.Holders

import org.grails.plugins.atmosphere_meteor.AtmosphereConfigurationHolder
import org.grails.plugins.atmosphere_meteor.AtmosphereMeteor
import org.grails.plugins.atmosphere_meteor.MeteorHandlerArtefactHandler
import org.grails.plugins.atmosphere_meteor.MeteorServletArtefactHandler

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AtmosphereMeteorGrailsPlugin {
	
	private Logger logger = LoggerFactory.getLogger("org.grails.plugins.atmosphere_meteor.AtmosphereMeteorGrailsPlugin")
	
	def version = "1.0.5"
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

	def applicationContext
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
		def environment = Environment.current.name
		// Check for configuration errors
		if (environment == "development") {
			printConfigurationErrors()
		}
	}

	def doWithSpring = {
	// Register atmosphereConfigurationHolder bean
		atmosphereConfigurationHolder(AtmosphereConfigurationHolder) { bean ->
			bean.factoryMethod = "getInstance"
		}
		// Register atmosphereMeteor bean
		atmosphereMeteor(AtmosphereMeteor) { bean ->
			bean.autowire = "byName"
		}
	}
	
	def doWithWebDescriptor = { webXml ->
		def config = AtmosphereConfigurationHolder.atmosphereMeteorConfig
		
		if (config) {
			config.servlets.each { name, parameters ->
				logger.debug "doWithWebDescriptor: $name, $parameters"
				def initParams = parameters.initParams
				
				appendToWebDescriptor(webXml, "servlet", {
					servlet {
						"servlet-name"(name)
						"servlet-class"(parameters.className)
						if (initParams != "none") {
							initParams?.each { param, value ->
								"init-param" {
									"param-name"(param)
									"param-value"(value)
								}
							}
						}
						"load-on-startup"(1)
						"async-supported"("true")
					}
				})
				appendToWebDescriptor(webXml, "servlet-mapping", ["servlet-name": name, "url-pattern": parameters.mapping])
			}
		} else {
			logger.error("AtmosphereConfigurationHolder.atmosphereMeteorConfig: config not found.")
		}
	}

	protected static lastChild(def node, def tag) {
		def children = node[tag]
		children[children.size() - 1]
	}

	protected static appendToWebDescriptor(def node, def tag, Closure append) {
		lastChild(node, tag) + append
	}

    protected static appendToWebDescriptor(def node, def tag, Map append) {
		lastChild(node, tag) + {
			"$tag" {
				append.each { k, v ->
					"$k"(v)
				}  
			}
		}
	}

	protected static serverInfo() {
		def servletContext = Holders.servletContext
		if (!servletContext) return
		def apiVersion = servletContext.effectiveMajorVersion
		List serverInfo = servletContext.serverInfo.tokenize("/")
		def serverName = serverInfo[0]
		def serverVersion = serverInfo[1]
		if (serverName.contains("jetty")) {
			serverName = "jetty"
		}
		if (serverName.contains("Tomcat")) {
			serverName = "tomcat"
		}
		[
			apiVersion   : apiVersion as Integer,
			serverName   : serverName,
			serverVersion: serverVersion
		]
	}

	protected static printConfigurationErrors() {
		Logger logger = LoggerFactory.getLogger("org.grails.plugins.atmosphere_meteor.AtmosphereMeteorGrailsPlugin")
		def serverInfo = serverInfo()
		def settings = BuildSettingsHolder.settings
		def tomcatNio = settings?.config?.grails?.tomcat?.nio
		def tomcatErrors = false
		def tomcatErrorNio = ""
		def tomcatErrorApi = ""

		if (!serverInfo) {
			logger.warn("Could not determine server information")
			return
		}

		// Tomcat errors
		if (serverInfo.serverName == "tomcat" && serverInfo.serverVersion.startsWith("7")) {
			if (tomcatNio != true) {
				logger.error("The atmosphere-meteor plugin requires in your BuildConfig.groovy: grails.tomcat.nio = true")
				tomcatErrors = true
				tomcatErrorNio = """
* grails.tomcat.nio = true					 *"""
			}
			if (serverInfo.apiVersion < 3) {
				logger.error("The atmosphere-meteor plugin requires in your BuildConfig.groovy:  grails.servlet.version = '3.0'")
				tomcatErrors = true
				tomcatErrorApi = """
* grails.servlet.version = "3.0"				   *"""
			}
			if (tomcatErrors) {
				println """
********************************************************************
* The atmosphere-meteor plugin requires the following settings in  *
* your grails-app/conf/BuildConfig.groovy:			 *$tomcatErrorNio$tomcatErrorApi
********************************************************************
"""
			}
		}

		// Jetty errors
		if (serverInfo.serverName == "jetty") {
			def jettyVersion = serverInfo.serverVersion.getAt(0) as Integer
			if (jettyVersion < 8) {
				def versionLine = "* It appears you are using version $jettyVersion.".padRight(67, " ")
				println """
********************************************************************
* The atmosphere-meteor plugin requires at least Jetty version 8.  *
$versionLine*
* Jetty documentation:					     *
* https://github.com/kensiprell/grails-atmosphere-meteor#jetty     *
********************************************************************
"""
			}
		}
	}
}
