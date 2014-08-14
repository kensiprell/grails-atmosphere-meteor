modules = {
	"atmosphere-meteor" {
		resource url: [plugin:"atmosphere-meteor", dir: "js", file:"atmosphere.js"],
				 disposition: "head"
	}
	"atmosphere-meteor-jquery" {
		dependsOn "jquery"
		resource url: [plugin:"atmosphere-meteor", dir: "js", file:"jquery.atmosphere.js"],
				 disposition: "head"
	}	
}
