// TODO change versions
def atmosphereVersion = "2.0.3"
def jacksonVersion = "1.9.13"
def jettyVersion = "8.1.13.v20130916"
def jettyPluginVersion = "2.0.3"

def processFileInplace(file, Closure processText) {
	def text = file.text
	file.write(processText(text))
}
def now = new Date()
def buildConfigFile = new File(basedir, "grails-app/conf/BuildConfig.groovy")
def buildConfigLines = buildConfigFile.text.readLines()
def atmosphereMeteorConfigFile = new File(basedir, "grails-app/conf/AtmosphereMeteorConfig.groovy")
def atmosphereMeteorResourcesFile = new File(basedir, "grails-app/conf/AtmosphereMeteorResources.groovy")
def grailsServletVersion = buildConfig.grails.servlet.version
def grailsTomcatNioExists = buildConfigLines.any { it =~ /grails.tomcat.nio/ && !(it =~ /\/\/.*grails.tomcat.nio/) }
boolean atmosphereMeteorResourcesFileExists = false
boolean isJetty = buildConfigLines.any { it =~ /jetty/ && !(it =~ /\/\/.*jetty/) }
boolean isTomcat = buildConfigLines.any { it =~ /tomcat/ && !(it =~ /\/\/.*tomcat/) }
boolean isAtmosphere = buildConfigLines.any { it =~ /org.atmosphere:atmosphere-runtime/ && !(it =~ /\/\/.*org.atmosphere:atmosphere-runtime/) }
boolean isJackson = buildConfigLines.any { it =~ /org.codehaus.jackson:jackson-core-asl/ && !(it =~ /\/\/.*org.codehaus.jackson:jackson-core-asl/) }

// Check Jetty version and if not Jetty 8 or 9, exit
// else modify BuildConfig.groovy dependencies to use version 8
if (isJetty) {
	def jettyDependencies = """
	dependencies {
		def jettyVersion = "${jettyVersion}" // Modified by atmosphere-meteor plugin on ${now}.
		provided(
				"org.eclipse.jetty:jetty-http:\\\${jettyVersion}",
				"org.eclipse.jetty:jetty-server:\\\${jettyVersion}",
				"org.eclipse.jetty:jetty-webapp:\\\${jettyVersion}",
				"org.eclipse.jetty:jetty-plus:\\\${jettyVersion}",
				"org.eclipse.jetty:jetty-security:\\\${jettyVersion}",
				"org.eclipse.jetty:jetty-websocket:\\\${jettyVersion}",
				"org.eclipse.jetty:jetty-continuation:\\\${jettyVersion}",
				"org.eclipse.jetty:jetty-jndi:\\\${jettyVersion}"
		) {
			excludes "commons-el", "ant", "sl4j-api", "sl4j-simple", "jcl104-over-slf4j", "xercesImpl", "xmlParserAPIs", "servlet-api", "mail", "commons-lang"
			excludes(
					[group: "org.eclipse.jetty.orbit", name: "javax.servlet"],
					[group: "org.eclipse.jetty.orbit", name: "javax.activation"],
					[group: "org.eclipse.jetty.orbit", name: "javax.mail.glassfish"],
					[group: "org.eclipse.jetty.orbit", name: "javax.transaction"]
			)
		}
"""
	def jettyPlugin = """
		runtime(":jetty:${jettyPluginVersion}") {  // Modified by atmosphere-meteor plugin on ${now}.
			excludes "jetty-http", "jetty-server", "jetty-webapp", "jetty-plus", "jetty-security", "jetty-websocket", "jetty-continuation", "jetty-jndi"
		}
"""

	if (buildConfigLines.any { it =~ /org.eclipse.jetty/ }) {
		def versionJettyVersion = null
		def versionJettyRuntime = null
		def versionJettyHttp = null
		def appJettyVersion

		// TODO check if commented out?
		buildConfigLines.each {
			if (it =~ /jettyVersion\s*=/) {
				def m = it =~ /=\s*['"](.*)['"]/
				versionJettyVersion = m[0][1]
			}
			// runtime "org.eclipse.jetty.aggregate:jetty-all:8.1.13.v20130916"
			if (it =~ /runtime\s+['"]org.eclipse.jetty.*:.*:.*['"]/) {
				def m = it =~ /['"]org.eclipse.jetty.*:.*:(.*)['"]/
				versionJettyRuntime = m[0][1]
			}
			// "org.eclipse.jetty:jetty-http:8.1.13.v20130916"
			if (it =~ /['"]org.eclipse.jetty:jetty-http:.*['"]/) {
				def m = it =~ /['"]org.eclipse.jetty:jetty-http:(.*)['"]/
				versionJettyHttp = m[0][1]
			}
		}
		appJettyVersion = [versionJettyVersion, versionJettyRuntime, versionJettyHttp].max()

		if (appJettyVersion.getAt(0) < 8.toString()) {
			def versionLine = "* It appears you are using version $appJettyVersion.".padRight(67, " ")
			println """
********************************************************************
* The atmosphere-meteor plugin requires at least Jetty version 8.  *
*                                                                  *
$versionLine*
*                                                                  *
* Jetty documentation:                                             *
* https://github.com/kensiprell/grails-atmosphere-meteor#jetty     *
*                                                                  *
* The plugin was not installed.                                    *
********************************************************************
"""
			return
		}
	} else {
		processFileInplace(buildConfigFile) { text ->
			text.replaceAll(/(?m)(^\s*dependencies\s*\{.*$)/, jettyDependencies)
		}
	}
	processFileInplace(buildConfigFile) { text ->
		text.replaceAll(/(?m)(^\s*build.*jetty.*$)/, jettyPlugin)
	}
}

// Create the directory for Atmosphere artefacts
ant.mkdir(dir: "${basedir}/grails-app/atmosphere")

// Copy the default plugin configuration file
if (!atmosphereMeteorConfigFile.exists()) {
	ant.copy(file: "${pluginBasedir}/src/templates/conf/AtmosphereMeteorConfig.groovy", todir: "${basedir}/grails-app/conf")
}

// Copy the default plugin resources file
if (atmosphereMeteorResourcesFile.exists()) {
	atmosphereMeteorResourcesFileExists = true
	ant.copy(file: "${basedir}/grails-app/conf/AtmosphereMeteorResources.groovy", tofile: "${basedir}/grails-app/conf/AtmosphereMeteorResources_ORIG.groovy")
}
ant.copy(file: "${pluginBasedir}/src/templates/conf/AtmosphereMeteorResources.groovy", todir: "${basedir}/grails-app/conf")

// Copy and modify BuildConfig.groovy
ant.copy(file: "${basedir}/grails-app/conf/BuildConfig.groovy", tofile: "${basedir}/grails-app/conf/BuildConfig_ORIG.groovy")

// Change grails.servlet.version to 3.0 in BuildConfig.groovy if necessary
if (grailsServletVersion != "3.0") {
	processFileInplace(buildConfigFile) { text ->
		text.replaceAll(/(?m)^grails\.servlet\.version.*"(.*)".*$/, """grails.servlet.version = "3.0" // Modified by atmosphere-meteor plugin on ${now}.""")
	}
}

// Add jackson-core-asl dependency in BuildConfig.groovy
if (isJackson) {
	processFileInplace(buildConfigFile) { text ->
		text.replaceAll(/(?m)compile ['"]org.codehaus.jackson:jackson-core-asl:(.*)['"].*$/, """compile 'org.codehaus.jackson:jackson-core-asl:${jacksonVersion}' // Modified by atmosphere-meteor plugin on ${now}.""")
	}
} else {
	processFileInplace(buildConfigFile) { text ->
		text.replaceAll(/(?m)(^\s*dependencies\s*\{.*$)/, """\$1
        compile 'org.codehaus.jackson:jackson-core-asl:${jacksonVersion}' // Added by atmosphere-meteor plugin on ${now}.
""")
	}
}

// Add atmosphere-runtime dependency in BuildConfig.groovy
if (isAtmosphere) {
	processFileInplace(buildConfigFile) { text ->
		text.replaceAll(/(?m)compile\(['"]org.atmosphere:atmosphere-runtime:(.*)['"]\).*$/, """compile('org.atmosphere:atmosphere-runtime:${atmosphereVersion}') { // Modified by atmosphere-meteor plugin on ${now}.""")
	}
} else {
	processFileInplace(buildConfigFile) { text ->
		text.replaceAll(/(?m)(^\s*dependencies\s*\{.*$)/, """\$1
        compile('org.atmosphere:atmosphere-runtime:${atmosphereVersion}') {  // Added by atmosphere-meteor plugin on ${now}.
            excludes 'slf4j-api'
        }
""")
	}
}

// Change grails.tomcat.nio to true in BuildConfig.groovy if necessary
if (isTomcat) {
	if (grailsTomcatNioExists) {
		processFileInplace(buildConfigFile) { text ->
			text.replaceAll(/(?m)^grails\.tomcat\.nio.*=(.*)$/, """grails.tomcat.nio = true // Modified by atmosphere-meteor plugin on ${now}.""")
		}
	} else {
		processFileInplace(buildConfigFile) { text ->
			text.replaceAll(/(?m)(^grails\.servlet\.version.*$)/, """\$1\ngrails.tomcat.nio = true // Added by atmosphere-meteor plugin on ${now}.""")
		}
	}
}

if (atmosphereMeteorResourcesFileExists) {
	println """
********************************************************************
* You have installed the atmosphere-meteor plugin.                 *
*                                                                  *
* Documentation:                                                   *
* https://github.com/kensiprell/grails-atmosphere-meteor           *
*                                                                  *
* BuildConfig.groovy was modified.                                 *
* The original was copied to BuildConfig_ORIG.groovy               *
*                                                                  *
* AtmosphereMeteorResources.groovy was replaced.                   *
* The original was copied to AtmosphereMeteorResources_ORIG.groovy *
*                                                                  *
* Next steps:                                                      *
* grails create-meteor-handler com.example.Default                 *
* grails create-meteor-servlet com.example.Default                 *
* Edit grails-app/conf/AtmosphereMeteorConfig.groovy               *
* Create controller, view, JavaScript client, etc.                 *
*                                                                  *
********************************************************************
"""
} else {
	println """
***********************************************************
* You have installed the atmosphere-meteor plugin.        *
*                                                         *
* Documentation:                                          *
* https://github.com/kensiprell/grails-atmosphere-meteor  *
*                                                         *
* BuildConfig.groovy was modified.                        *
* The original was copied to BuildConfig_ORIG.groovy      *
*                                                         *
* Next steps:                                             *
* grails create-meteor-handler com.example.Default        *
* grails create-meteor-servlet com.example.Default        *
* Edit grails-app/conf/AtmosphereMeteorConfig.groovy      *
* Create controller, view, JavaScript client, etc.        *
*                                                         *
***********************************************************
"""
}

