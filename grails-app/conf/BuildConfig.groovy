grails.servlet.version = "3.0"
grails.tomcat.nio = true
grails.project.work.dir = "target"
grails.project.dependency.resolver = "maven"

grails.project.dependency.distribution = {
	remoteRepository(id: "localPluginReleases", url: "http://localhost:8081/artifactory/plugins-release-local/")
	remoteRepository(id: "localPluginSnapshots", url: "http://localhost:8081/artifactory/plugins-snapshot-local/")
}

grails.project.dependency.resolution = {
	inherits "global"
	log "warn"

	repositories {
		grailsCentral()
		mavenLocal()
		mavenCentral()
	}

	dependencies {
		compile "org.atmosphere:atmosphere-runtime:2.3.2", {
			excludes "slf4j-api"
		}
		compile "org.codehaus.jackson:jackson-core-asl:1.9.13"
	}

	plugins {
		build ":release:3.0.1", ":rest-client-builder:2.1.1", {
			export = false
		}
		compile ":asset-pipeline:2.2.3", {
			export = false
		}
		runtime ":resources:1.2.14", {
			export = false
		}
	}
}
