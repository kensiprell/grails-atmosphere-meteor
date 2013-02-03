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

package org.grails.plugins.atmosphere2

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

	static ConfigObject getAtmosphere2Config() {
		ConfigObject config
		GroovyClassLoader classLoader = new GroovyClassLoader()
		String environment = Environment.getCurrent().toString()
		def slurper = new ConfigSlurper(environment)
		try {
			config = slurper.parse(classLoader.loadClass('Atmosphere2Config'))
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

