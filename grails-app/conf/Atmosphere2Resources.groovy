modules = {
	'atmosphere2' {
		dependsOn 'jquery'
		resource id: 'js', url: [plugin: 'atmosphere2', dir: 'js', file: "jquery.atmosphere.js"],
				disposition: 'head', nominify: true
	}
}