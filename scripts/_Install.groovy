def atmosphereMeteorConfigFile = new File(basedir, "grails-app/conf/AtmosphereMeteorConfig.groovy")

// Create the directory for Atmosphere artefacts
ant.mkdir(dir: "${basedir}/grails-app/atmosphere")

// Copy the default plugin configuration file
if (!atmosphereMeteorConfigFile.exists()) {
	ant.copy(file: "${pluginBasedir}/src/templates/conf/AtmosphereMeteorConfig.groovy", todir: "${basedir}/grails-app/conf")
}

println """
********************************************************************
* You have installed the atmosphere-meteor plugin.                 *
*                                                                  *
* Documentation:                                                   *
* https://github.com/kensiprell/grails-atmosphere-meteor           *
*                                                                  *
* Next steps:                                                      *
* grails create-meteor-handler com.example.Default                 *
* grails create-meteor-servlet com.example.Default                 *
* Edit grails-app/conf/AtmosphereMeteorConfig.groovy               *
* Create controller, view, JavaScript client, etc.                 *
*                                                                  *
********************************************************************
"""
