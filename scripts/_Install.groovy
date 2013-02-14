// Define some variables
def processFileInplace(file, Closure processText) {
	def text = file.text
	file.write(processText(text))
}
def buildConfigFile = new File(basedir, "grails-app/conf/BuildConfig.groovy")
def grailsServletVersion = buildConfig.grails.servlet.version
def grailsTomcatNio = buildConfig.grails.tomcat.nio
boolean isTomcat = buildConfigFile.text.readLines().any { it =~ /tomcat/ && !(it =~ /\/\/.*tomcat/) }

// Create the directory for Atmosphere artefacts
ant.mkdir(dir: "${basedir}/grails-app/atmosphere")

// Copy the default plugin configuration file
ant.copy(file: "${pluginBasedir}/grails-app/conf/Atmosphere2ConfigDefault.groovy", tofile: "${basedir}/grails-app/conf/Atmosphere2Config.groovy")

// Copy the default plugin resources file
ant.copy(file: "${pluginBasedir}/grails-app/conf/Atmosphere2Resources.groovy", toDir: "${basedir}/grails-app/conf")

// Modify BuildConfig.groovy
ant.copy(file: "${basedir}/grails-app/conf/BuildConfig.groovy", tofile: "${basedir}/grails-app/conf/BuildConfig_ORIG.groovy")

// Change grails.servlet.version to 3.0 in BuildConfig.groovy if necessary
if (grailsServletVersion != "3") {
	processFileInplace(buildConfigFile) { text ->
		text.replaceAll(/(?m)^grails\.servlet\.version.*"(.*)".*$/, """grails.servlet.version = "3" // Modified by Atmosphere2 plugin on ${new Date()}. Previous version was ${grailsServletVersion}.""")
	}
}

// Change grails.tomcat.nio to true in BuildConfig.groovy if necessary
if (isTomcat) {
	if (grailsTomcatNio != true && grailsTomcatNio.size() != 0) {
		processFileInplace(buildConfigFile) { text ->
			text.replaceAll(/(?m)^grails\.tomcat\.nio.*=(.*)$/, """grails.tomcat.nio = true // Modified by Atmosphere2 plugin on ${new Date()}. Previous value was ${grailsTomcatNio}.""")
		}
	}
	if (grailsTomcatNio.size() == 0) {
		processFileInplace(buildConfigFile) { text ->
			text.replaceAll(/(?m)(^grails\.servlet\.version.*$)/, """\$1\ngrails.tomcat.nio = true // Added by Atmosphere2 plugin on ${new Date()}.""")
		}
	}
}

// Add atmosphere-runtime dependency in BuildConfig.groovy
processFileInplace(buildConfigFile) { text ->
	text.replaceAll(/(?m)(^\s*dependencies\s*\{.*$)/, """\$1
        compile('org.atmosphere:atmosphere-runtime:1.0.9') {  // Added by Atmosphere2 plugin on ${new Date()}.
            excludes 'slf4j-api', 'atmosphere-ping'
        }
""")
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
*******************************************************
* You have installed the Atmosphere2 plugin.          *
*                                                     *
* Documentation:                                      *
* https://github.com/kensiprell/grails-atmosphere2    *
*                                                     *
* BuildConfig.groovy was modified.                    *
* The original was copied to BuildConfig_ORIG.groovy  *
*                                                     *
* Next steps:                                         *
* grails create-meteor-handler com.example.Default    *
* grails create-meteor-servlet com.example.Default    *
* Edit grails-app/conf/Atmosphere2Config.groovy       *
* Create controller, view, JavaScript client, etc.    *
*                                                     *
*******************************************************
'''
