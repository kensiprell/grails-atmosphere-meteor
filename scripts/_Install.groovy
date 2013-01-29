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



//
// This script is executed by Grails after plugin was installed to project.
// This script is a Gant script so you can use all special variables provided
// by Gant (such as 'baseDir' which points on project base dir). You can
// use 'ant' to access a global instance of AntBuilder
//
// For example you can create directory under project tree:
//
//    ant.mkdir(dir:"${basedir}/grails-app/jobs")
//

// Create the directory for Atmosphere artefacts
def atmosphereDir = "${basedir}/grails-app/atmosphere"
ant.mkdir(dir: atmosphereDir)

// Copy the default configuration file
ant.copy(file: "${pluginBasedir}/grails-app/conf/Atmosphere2ConfigDefault.groovy", tofile: "${basedir}/grails-app/conf/Atmosphere2Config.groovy")

// Copy the default resources file
ant.copy(file: "${pluginBasedir}/grails-app/conf/Atmosphere2Resources.groovy", toDir: "${basedir}/grails-app/conf")

// Modify BuildConfig.groovy
ant.copy(file: "${basedir}/grails-app/conf/BuildConfig.groovy", tofile: "${basedir}/grails-app/conf/BuildConfig_ORIG.groovy")
def processFileInplace(file, Closure processText) {
	def text = file.text
	file.write(processText(text))
}
def buildConfigFile = new File("${basedir}/grails-app/conf/BuildConfig.groovy")
def buildConfigConfig = new ConfigSlurper().parse(new File("${basedir}/grails-app/conf/BuildConfig.groovy").toURI().toURL())
def grailsServletVersion = buildConfigConfig.grails.servlet.version
def grailsTomcatNio = buildConfigConfig.grails.tomcat.nio
if (grailsServletVersion != "3") {
	processFileInplace(buildConfigFile) { text ->
		text.replaceAll(/(?m)^grails\.servlet\.version.*"(.*)".*$/, """grails.servlet.version = "3" // Modified by Atmosphere2 plugin on ${new Date()}. Previous version was ${grailsServletVersion}.""")
	}
}
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
processFileInplace(buildConfigFile) { text ->
	text.replaceAll(/(?m)(^\s*dependencies\s*\{.*$)/, """\$1
        compile('org.atmosphere:atmosphere-runtime:1.0.9') {  // Added by Atmosphere2 plugin on ${new Date()}.
            excludes 'slf4j-api', 'atmosphere-ping'
        }
""")
}

// Write the context.xml file in META-INF and WEB-INF
def contextDotXml = """\
<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<Context>
	<Loader delegate=\"true\"/>
</Context>"""
def metaInf = new File("$basedir/web-app/META-INF/")
def webInf = new File("$basedir/web-app/WEB-INF/")

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
*                                                     *
*******************************************************

'''
