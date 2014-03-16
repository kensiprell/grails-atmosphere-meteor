modules = {
	"atmosphere-meteor" {
		resource url: "/js/atmosphere.js", disposition: "head"
	}
	"atmosphere-meteor-jquery" {
		dependsOn "jquery"
		resource url: "/js/jquery.atmosphere.js", disposition: "head"
	}
}