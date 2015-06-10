package org.grails.plugins.atmosphere_meteor

import grails.util.Environment
import grails.util.Holders
import groovy.util.logging.Log4j

import org.atmosphere.cpr.AtmosphereFramework

@Log4j
@Singleton
class AtmosphereConfigurationHolder {

	@Deprecated
	static AtmosphereFramework framework

	static ConfigObject getPluginConfig() {
		GroovyClassLoader classLoader = new GroovyClassLoader()
		def slurper = new ConfigSlurper(Environment.current.name)
		def defaultConfigClass = classLoader.loadClass("AtmosphereMeteorPluginConfig")
		def defaultConfig = slurper.parse(defaultConfigClass).grails.plugin.atmosphere_meteor.plugin
		def grailsApplication = Holders.grailsApplication
		def customConfig = grailsApplication.config.grails.plugin?.atmosphere_meteor?.plugin
		def config = customConfig ? defaultConfig.merge(customConfig) : defaultConfig
		return config
	}
	
	static ConfigObject getAtmosphereMeteorConfig() {
		ConfigObject config = null
		GroovyClassLoader classLoader = new GroovyClassLoader()
		def slurper = new ConfigSlurper(Environment.current.name)
		try {
			config = slurper.parse(classLoader.loadClass("AtmosphereMeteorConfig"))
		}
		catch (e) {
			config = slurper.parse(classLoader.loadClass("AtmosphereMeteorDefaultConfig"))
			log.warn "AtmosphereMeteorConfig not found: using AtmosphereMeteorDefaultConfig."
		}
		return config
	}
}
