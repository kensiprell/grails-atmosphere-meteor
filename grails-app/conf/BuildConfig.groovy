grails.servlet.version = "3.0"
grails.tomcat.nio = true
grails.project.work.dir = "target"

grails.project.dependency.resolution = {

	inherits "global"
	log "warn"
	legacyResolve true

	repositories {
		grailsCentral()
		mavenLocal()
		mavenCentral()
	}

	dependencies {
		compile("org.atmosphere:atmosphere-runtime:1.0.13") {
			excludes "slf4j-api", "atmosphere-ping"
		}
	}

	plugins {
		runtime ":jquery:1.8.3"

		runtime ":resources:1.2"

		build ":release:2.2.0", ":rest-client-builder:1.0.3", {
			export = false
		}

		build ":tomcat:$grailsVersion", {
			export = false
		}

		compile  ":hibernate:$grailsVersion", {
			export = false
		}
	}
}
