package org.grails.plugins.atmosphere_meteor

import grails.util.Environment

import javax.servlet.ServletContext
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.GrailsPluginManager
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

/*
 Courtesy of Burt Beckwith
 http://burtbeckwith.com/blog/?p=1017
 */

@Singleton
class ApplicationContextHolder implements ApplicationContextAware {

	private ApplicationContext ctx

	private static final Map<String, Object> TEST_BEANS = [:]

	void setApplicationContext(ApplicationContext applicationContext) {
		ctx = applicationContext
	}

	static ApplicationContext getApplicationContext() {
		getInstance().ctx
	}

	static Object getBean(String name) {
		TEST_BEANS[name] ?: getApplicationContext().getBean(name)
	}

	static GrailsApplication getGrailsApplication() {
		getBean("grailsApplication")
	}

	static ConfigObject getConfig() {
		getGrailsApplication().config
	}

	static ServletContext getServletContext() {
		getBean("servletContext")
	}

	static GrailsPluginManager getPluginManager() {
		getBean("pluginManager")
	}

	static ConfigObject getAtmosphereMeteorConfig() {
		ConfigObject config
		GroovyClassLoader classLoader = new GroovyClassLoader()
		String environment = Environment.getCurrent().toString()
		def slurper = new ConfigSlurper(environment)
		try {
			config = slurper.parse(classLoader.loadClass('AtmosphereMeteorConfig'))
		}
		catch (e) {
		}
		return config
	}

	// For testing
	static void registerTestBean(String name, bean) {
		TEST_BEANS[name] = bean
	}

	// For testing
	static void unregisterTestBeans() {
		TEST_BEANS.clear()
	}
}

