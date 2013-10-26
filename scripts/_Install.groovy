// TODO change versions
def atmosphereVersion = "2.0.3"
def jacksonVersion = "1.9.13"

def processFileInplace(file, Closure processText) {
	def text = file.text
	file.write(processText(text))
}

def now = new Date()
def buildConfigFile = new File(basedir, "grails-app/conf/BuildConfig.groovy")
def buildConfigLines = buildConfigFile.text.readLines()
def atmosphereMeteorConfigFile = new File(basedir, "grails-app/conf/AtmosphereMeteorConfig.groovy")
def atmosphereMeteorResourcesFile = new File(basedir, "grails-app/conf/AtmosphereMeteorResources.groovy")
def grailsServletVersion = buildConfig.grails.servlet.version.toString()
def grailsTomcatNio = buildConfig.grails.tomcat.nio
def grailsTomcatNioExists = buildConfigLines.any { it =~ /grails.tomcat.nio/ && !(it =~ /\/\/.*grails.tomcat.nio/) }
def buildConfigText = ""
def atmosphereMeteorResourcesText = ""
boolean atmosphereMeteorResourcesFileExists = false
boolean buildConfigCopied = false
boolean isTomcat = buildConfigLines.any { it =~ /tomcat/ && !(it =~ /\/\/.*tomcat/) }

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

// Ensure grails.servlet.version is set to to 3.0 in BuildConfig.groovy
if (grailsServletVersion != "3.0") {
	buildConfigCopied = true
	ant.copy(file: "${basedir}/grails-app/conf/BuildConfig.groovy", tofile: "${basedir}/grails-app/conf/BuildConfig_ORIG.groovy")
	processFileInplace(buildConfigFile) { text ->
		text.replaceAll(/(?m)^grails\.servlet\.version.*"(.*)".*$/, """grails.servlet.version = "3.0" // Modified by atmosphere-meteor plugin on ${now}.""")
	}
}

// Ensure grails.tomcat.nio is set to true in BuildConfig.groovy
if (isTomcat) {
	if (grailsTomcatNioExists) {
		if (grailsTomcatNio != true) {
			ant.copy(file: "${basedir}/grails-app/conf/BuildConfig.groovy", tofile: "${basedir}/grails-app/conf/BuildConfig_ORIG.groovy")
			buildConfigCopied = true
			processFileInplace(buildConfigFile) { text ->
				text.replaceAll(/(?m)^grails\.tomcat\.nio.*=(.*)$/, """grails.tomcat.nio = true // Modified by atmosphere-meteor plugin on ${now}.""")
			}
		}
	} else {
		ant.copy(file: "${basedir}/grails-app/conf/BuildConfig.groovy", tofile: "${basedir}/grails-app/conf/BuildConfig_ORIG.groovy")
		buildConfigCopied = true
		processFileInplace(buildConfigFile) { text ->
			text.replaceAll(/(?m)(^grails\.servlet\.version.*$)/, """\$1\ngrails.tomcat.nio = true // Added by atmosphere-meteor plugin on ${now}.""")
		}
	}
}

if (atmosphereMeteorResourcesFileExists) {
	atmosphereMeteorResourcesText = """
* AtmosphereMeteorResources.groovy was replaced.                   *
* The original was copied to AtmosphereMeteorResources_ORIG.groovy *
*                                                                  *"""
}

if (buildConfigCopied) {
	buildConfigText = """
* BuildConfig.groovy was modified.                                 *
* The original was copied to BuildConfig_ORIG.groovy               *
*                                                                  *"""
}

println """
********************************************************************
* You have installed the atmosphere-meteor plugin.                 *
*                                                                  *
* Documentation:                                                   *
* https://github.com/kensiprell/grails-atmosphere-meteor           *
*                                                                  *$buildConfigText$atmosphereMeteorResourcesText
* Next steps:                                                      *
* grails create-meteor-handler com.example.Default                 *
* grails create-meteor-servlet com.example.Default                 *
* Edit grails-app/conf/AtmosphereMeteorConfig.groovy               *
* Create controller, view, JavaScript client, etc.                 *
*                                                                  *
********************************************************************
"""
