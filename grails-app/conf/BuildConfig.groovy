grails.servlet.version = "3.0"
grails.tomcat.nio = true
grails.project.work.dir = "target"

grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'
	legacyResolve true

	repositories {
		grailsCentral()
		mavenLocal()
		mavenCentral()
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
