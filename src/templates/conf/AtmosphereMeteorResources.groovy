modules = {
	'atmosphere-meteor' {
		dependsOn 'jquery'
		resource id: 'js', url: [plugin: 'atmosphere-meteor', dir: 'js', file: "atmosphere.js"],
				disposition: 'head', nominify: true
	}
}