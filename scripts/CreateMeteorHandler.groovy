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

includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsCreateArtifacts")

target('default': "Creates a new Atmosphere Meteor handler") {
	depends(checkVersion, parseArguments)

	def type = "MeteorHandler"
	promptForName(type: type)

	for (name in argsMap["params"]) {
		name = purgeRedundantArtifactSuffix(name, type)
		createArtifact(name: name, suffix: type, type: type, path: "grails-app/atmosphere")

		createUnitTest(name: name, suffix: type, superClass: "MeteorHandlerUnitTestCase")
	}
}