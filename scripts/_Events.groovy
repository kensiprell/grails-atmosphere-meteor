import groovy.xml.StreamingMarkupBuilder

eventCompileEnd = {
	if (!isPluginProject) {
		buildConfiguration(basedir)
	}
}

def buildConfiguration(basedir) {
	def config = new ConfigSlurper().parse(new File("${basedir}/grails-app/conf/AtmosphereMeteorConfig.groovy").toURI().toURL())
	def sitemeshXml = new File("$basedir/web-app/WEB-INF/sitemesh.xml")
	def defaultMapping = config.defaultMapping ?: '/atmosphere/*'

	// Create atmosphere-meteor-decorators.xml in WEB-INF
	def decoratorsDotXml = """\
<decorators>
    <excludes>
        <pattern>$defaultMapping</pattern>
    </excludes>
</decorators>"""

	new File("$basedir/web-app/WEB-INF/atmosphere-meteor-decorators.xml").write decoratorsDotXml

	// Modify the sitemesh.xml file in WEB-INF if necessary
	if (!sitemeshXml.exists())
		return
	def doc = new XmlSlurper().parse(sitemeshXml)
	if (!doc.excludes.find { it.@file == '/WEB-INF/atmosphere-meteor-decorators.xml' }.size()) {
		doc.appendNode({ excludes(file: '/WEB-INF/atmosphere-meteor-decorators.xml') })
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
