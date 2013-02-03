class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(controller: 'AtmosphereTest', action: 'index')
		"500"(view:'/error')
	}
}
