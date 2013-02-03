grails.servlet.version = "3.0"
grails.tomcat.nio = true
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
	// inherit Grails' default dependencies
	inherits("global") {
		// uncomment to disable ehcache
		// excludes 'ehcache'
	}
	log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
	legacyResolve true // whether to do a secondary resolve on plugin installation, not advised but set here for backwards compatibility
	repositories {
		grailsCentral()
		grailsHome()
		grailsPlugins()
		mavenCentral()
		mavenLocal()
		//mavenRepo "http://snapshots.repository.codehaus.org"
		//mavenRepo "http://repository.codehaus.org"
		//mavenRepo "http://download.java.net/maven/2/"
		//mavenRepo "http://repository.jboss.com/maven2/"
	}
	dependencies {
		compile('org.atmosphere:atmosphere-runtime:1.0.9') {
			excludes 'slf4j-api', 'atmosphere-ping'
		}
	}

	plugins {
		build	":tomcat:$grailsVersion"
		build	":release:2.2.0"

		compile	":hibernate:$grailsVersion"

		runtime	":jquery:1.8.0"
		runtime	":resources:1.2.RC3"
	}
}
