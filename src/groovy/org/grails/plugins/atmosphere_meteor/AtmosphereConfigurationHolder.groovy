package org.grails.plugins.atmosphere_meteor

import grails.util.Environment
import groovy.util.logging.Log4j

import org.atmosphere.cpr.AtmosphereFramework

@Log4j
@Singleton
class AtmosphereConfigurationHolder {

	@Deprecated
	static AtmosphereFramework framework
	
	static ConfigObject getAtmosphereMeteorConfig() {
		ConfigObject config = null
		GroovyClassLoader classLoader = new GroovyClassLoader()
		String environment = Environment.getCurrent().toString()
		def slurper = new ConfigSlurper(environment)
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
