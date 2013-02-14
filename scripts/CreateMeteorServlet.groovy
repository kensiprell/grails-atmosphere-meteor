includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsCreateArtifacts")

target('default': "Creates a new Atmosphere Meteor servlet") {
	depends(checkVersion, parseArguments)

	def type = "MeteorServlet"
	promptForName(type: type)

	for (name in argsMap["params"]) {
		name = purgeRedundantArtifactSuffix(name, type)
		createArtifact(name: name, suffix: type, type: type, path: "grails-app/atmosphere")

		createUnitTest(name: name, suffix: type, superClass: "MeteorServletUnitTestCase")
	}
}
