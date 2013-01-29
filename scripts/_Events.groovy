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



import groovy.xml.StreamingMarkupBuilder

eventCompileEnd = {
	if (!isPluginProject) {
		buildConfiguration(basedir)
	}
}

def buildConfiguration(basedir) {
	def config = new ConfigSlurper().parse(Atmosphere2Config)

	def sitemeshXml = new File("$basedir/web-app/WEB-INF/sitemesh.xml")
	def defaultUrl = config.defaultUrl ?: '/jabber/*'

	// Generate the atmosphere2-decorators.xml file in WEB-INF
	def decoratorsDotXml = """\
<decorators>
    <excludes>
        <pattern>$defaultUrl</pattern>
    </excludes>
</decorators>"""

	new File("$basedir/web-app/WEB-INF/atmosphere2-decorators.xml").write decoratorsDotXml

	// Modify the sitemesh.xml file in WEB-INF if necessary
	if (!sitemeshXml.exists())
		return
	def doc = new XmlSlurper().parse(sitemeshXml)
	if (!doc.excludes.find { it.@file == '/WEB-INF/atmosphere2-decorators.xml' }.size()) {
		doc.appendNode({ excludes(file: '/WEB-INF/atmosphere2-decorators.xml') })
		// Save the XML document with pretty print
		def xml = new StreamingMarkupBuilder().bind {
			mkp.yield(doc)
		}
		def node = new XmlParser().parseText(xml.toString())
		sitemeshXml.withWriter {
			new XmlNodePrinter(new PrintWriter(it)).print(node)
		}
	}
}
