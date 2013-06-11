modules = {
	'atmosphere-meteor' {
		// TODO remove depends
		dependsOn 'jquery'
		// TODO change to atmosphere.js
		resource id: 'js', url: [plugin: 'atmosphere-meteor', dir: 'js', file: "jquery.atmosphere.js"],
				disposition: 'head', nominify: true
	}
}