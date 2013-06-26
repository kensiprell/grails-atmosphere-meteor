modules = {
	'atmosphere-meteor' {
		resource id: 'js',
				url: [plugin: 'atmosphere-meteor', dir: 'js', file: "atmosphere.js"],
				disposition: 'head', nominify: true
	}
	'atmosphere-meteor-jquery' {
		dependsOn 'jquery'
		resource id: 'js',
				url: [plugin: 'atmosphere-meteor', dir: 'js', file: "jquery.atmosphere.js"],
				disposition: 'head', nominify: true
	}
}