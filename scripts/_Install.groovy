// Define some variables
def processFileInplace(file, Closure processText) {
	def text = file.text
	file.write(processText(text))
}

def now = new Date()
// TODO update version here and in README.md
def atmosphereVersion = "1.1.0.RC4"
// TODO update version
def jacksonVersion = "1.1.1"
def buildConfigFile = new File(basedir, "grails-app/conf/BuildConfig.groovy")
def atmosphereMeteorConfigFile = new File(basedir, "grails-app/conf/AtmosphereMeteorConfig.groovy")
def atmosphereMeteorResourcesFile = new File(basedir, "grails-app/conf/AtmosphereMeteorResources.groovy")
def grailsServletVersion = buildConfig.grails.servlet.version
def grailsTomcatNio = buildConfig.grails.tomcat.nio
boolean isTomcat = buildConfigFile.text.readLines().any { it =~ /tomcat/ && !(it =~ /\/\/.*tomcat/) }
boolean isAtmosphere = buildConfigFile.text.readLines().any { it =~ /org.atmosphere:atmosphere-runtime/ && !(it =~ /\/\/.*org.atmosphere:atmosphere-runtime/) }
boolean isJackson = buildConfigFile.text.readLines().any { it =~ /org.codehaus.jackson:jackson-core-asl/ && !(it =~ /\/\/.*org.codehaus.jackson:jackson-core-asl/) }

// Create the directory for Atmosphere artefacts
ant.mkdir(dir: "${basedir}/grails-app/atmosphere")

// Copy the default plugin configuration file
if (!atmosphereMeteorConfigFile.exists()) {
	ant.copy(file: "${pluginBasedir}/src/templates/conf/AtmosphereMeteorConfig.groovy", todir: "${basedir}/grails-app/conf")
}

// Copy the default plugin resources file
if (!atmosphereMeteorResourcesFile.exists()) {
	ant.copy(file: "${pluginBasedir}/src/templates/conf/AtmosphereMeteorResources.groovy", todir: "${basedir}/grails-app/conf")
}

// Modify BuildConfig.groovy
ant.copy(file: "${basedir}/grails-app/conf/BuildConfig.groovy", tofile: "${basedir}/grails-app/conf/BuildConfig_ORIG.groovy")

// Change grails.servlet.version to 3.0 in BuildConfig.groovy if necessary
if (grailsServletVersion != "3.0") {
	processFileInplace(buildConfigFile) { text ->
		text.replaceAll(/(?m)^grails\.servlet\.version.*"(.*)".*$/, """grails.servlet.version = "3.0" // Modified by atmosphere-meteor plugin on ${now}. Previous version was ${grailsServletVersion}.""")
	}
}
// Change grails.tomcat.nio to true in BuildConfig.groovy if necessary
if (isTomcat) {
	if (grailsTomcatNio != true) {
		processFileInplace(buildConfigFile) { text ->
			text.replaceAll(/(?m)^grails\.tomcat\.nio.*=(.*)$/, """grails.tomcat.nio = true // Modified by atmosphere-meteor plugin on ${now}. Previous value was ${grailsTomcatNio}.""")
		}
	}
	if (!grailsTomcatNio) {
		processFileInplace(buildConfigFile) { text ->
			text.replaceAll(/(?m)(^grails\.servlet\.version.*$)/, """\$1\ngrails.tomcat.nio = true // Added by atmosphere-meteor plugin on ${now}.""")
		}
	}
}

// Add atmosphere-runtime dependency in BuildConfig.groovy
if (isAtmosphere) {
	processFileInplace(buildConfigFile) { text ->
		text.replaceAll(/(?m)compile\('org.atmosphere:atmosphere-runtime.*$/, """compile('org.atmosphere:atmosphere-runtime:${atmosphereVersion}') { // Modified by atmosphere-meteor plugin on ${now}.""")
	}
} else {
	processFileInplace(buildConfigFile) { text ->
		text.replaceAll(/(?m)(^\s*dependencies\s*\{.*$)/, """\$1
        compile('org.atmosphere:atmosphere-runtime:${atmosphereVersion}') {  // Added by atmosphere-meteor plugin on ${now}.
            excludes 'slf4j-api', 'atmosphere-ping'
        }
""")
	}
}

// Add jackson-core-asl dependency in BuildConfig.groovy
if (isJackson) {
	processFileInplace(buildConfigFile) { text ->
		text.replaceAll(/(?m)compile "org.codehaus.jackson:jackson-core-asl.*$/, """compile "org.codehaus.jackson:jackson-core-asl:${jacksonVersion}" // Modified by atmosphere-meteor plugin on ${now}.""")
	}
} else {
	processFileInplace(buildConfigFile) { text ->
		text.replaceAll(/(?m)(^\s*dependencies\s*\{.*$)/, """\$1
        compile "org.codehaus.jackson:jackson-core-asl:${jacksonVersion}" // Added by atmosphere-meteor plugin on ${now}.
""")
	}
}

// Create context.xml in META-INF and WEB-INF
	def contextDotXml = """\
<?xml version="1.0" encoding="UTF-8"?>
<Context>
	<Loader delegate="true"/>
</Context>"""
	def metaInf = new File(basedir, "web-app/META-INF/")
	def webInf = new File(basedir, "web-app/WEB-INF/")
	if (!metaInf.exists())
		metaInf.mkdirs()
	if (!webInf.exists())
		webInf.mkdirs()
	new File(metaInf, "context.xml").write contextDotXml
	new File(webInf, "context.xml").write contextDotXml

	println '''
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
'''
