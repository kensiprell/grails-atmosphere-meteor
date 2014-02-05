## Grails plugin for integrating the Atmosphere Framework

[Atmosphere Wiki](https://github.com/Atmosphere/atmosphere/wiki)

[Atmosphere Google Group](https://groups.google.com/forum/?fromgroups#!forum/atmosphere-framework)

The plugin has been tested in the following environment, using the [grails-atmosphere-meteor-sample](https://github.com/kensiprell/grails-atmosphere-meteor-sample) application and [grails-plugin-test-script](https://github.com/kensiprell/grails-plugin-test-script):

* atmosphere-runtime 2.1.0

* OSX 10.9.1

* JDK 1.7.0_51

* Grails versions 2.1.0 through 2.3.5

* Tomcat (version depends on Grails version)

* Jetty 8.1.13.v20130916


If you have a question, problem, suggestion, or want to report a bug, please submit an [issue](https://github.com/kensiprell/grails-atmosphere-meteor/issues?state=open). I will reply as soon as I can.

[Planned Changes](https://github.com/kensiprell/grails-atmosphere-meteor/wiki/Planned-Changes)

[Release Notes](https://github.com/kensiprell/grails-atmosphere-meteor/wiki/Release-Notes)

The plugin uses the following components of the Atmosphere Framework:

* [atmosphere.js / jquery.atmosphere.js](https://github.com/Atmosphere/atmosphere-javascript)

* [MeteorServlet](http://atmosphere.github.com/atmosphere/apidocs/org/atmosphere/cpr/MeteorServlet.html)

* [ReflectorServletProcessor](http://atmosphere.github.com/atmosphere/apidocs/org/atmosphere/handler/ReflectorServletProcessor.html)

* [DefaultBroadcaster](http://atmosphere.github.com/atmosphere/apidocs/org/atmosphere/cpr/DefaultBroadcaster.html)

* [SimpleBroadcaster](http://atmosphere.github.com/atmosphere/apidocs/org/atmosphere/util/SimpleBroadcaster.html)

## How It Works

### Java Servlet

The plugin is designed to create and use a servlet for each main or significant URL pattern. For example, if you download the [sample application](https://github.com/kensiprell/grails-atmosphere-meteor-sample), you will see that a servlet is created for each URL pattern below:

	/atmosphere/chat/*

	/atmosphere/notification/*

	/atmosphere/public/*

The servlets are registered programmatically and are not defined in web.xml.

### Configuration

All servlets are defined in grails-app/conf/AtmosphereMeteorConfig.groovy.

 grails-app/conf/AtmosphereResources.groovy has modules for atmosphere.js and jquery.atmosphere.js.

### MeteorServlet Class

The create-meteor-servlet script creates a class in grails-app/atmosphere that extends Atmosphere's MeteorServlet. You could probably use a single class throughout your application.

Although the [sample application](https://github.com/kensiprell/grails-atmosphere-meteor-sample) uses the same MeteorServlet class for each URL, you can easily use a different class. Of course, each of the URL patterns above can be further divided using a combination of request headers, path, etc. For example, a chat room could be established under /atmosphere/chat/private-room that is serviced by the same servlet, MeteorServlet, and MeteorHandler classes as /atmosphere/chat/*.

### MeteorHandler Class

The create-meteor-handler script creates a class in grails-app/atmosphere that extends HttpServlet. This is where you customize how the HTTP requests and responses (including Atmosphere Broadcaster) are handled.

## Plugin Installation, Configuration, and Use

### Installion
Edit your BuildConfig.groovy:

```
plugins {
    // other plugins
    compile ":atmosphere-meteor:0.7.1"
    // other plugins
}
```

The plugin will make some minor changes to your grails-app/conf/BuildConfig.groovy on installation. Your original file will be copied to grails-app/conf/BuildConfig_ORIG.groovy. 

If not already set, the plugin will change your BuildConfig.groovy to use the Servlet 3.0 API and the Tomcat NIO connector.

```
grails.servlet.version = "3.0"
grails.tomcat.nio = true
```


### Jetty

The current [Jetty Plugin](http://www.grails.org/plugins/jetty) installs Jetty 7 with the Servlet 2.5 API, and the plugin requires the Servlet 3.0 API. Functional tests and the run-war command fail with the Jetty Plugin and Grails 2.3. Thanks to help from [Igor Poteryaev](http://jetty.4.x6.nabble.com/template/NamlServlet.jtp?macro=user_nodes&user=360626), we can work around these problems.


The atmosphere-meteor plugin automatically resolves the issues with functional testing and the run-war command.
 
You can update your Jetty version by modifying your BuildConfig.groovy using Igor's recommendation:

```
dependencies {
	// other dependencies
	def jettyVersion = "8.1.13.v20130916"
	provided(
		"org.eclipse.jetty:jetty-http:$jettyVersion",
		"org.eclipse.jetty:jetty-server:$jettyVersion",
		"org.eclipse.jetty:jetty-webapp:$jettyVersion",
		"org.eclipse.jetty:jetty-plus:$jettyVersion",
		"org.eclipse.jetty:jetty-security:$jettyVersion",
		"org.eclipse.jetty:jetty-websocket:$jettyVersion",
		"org.eclipse.jetty:jetty-continuation:$jettyVersion",
		"org.eclipse.jetty:jetty-jndi:$jettyVersion"
	) {
   		excludes "commons-el","ant", "sl4j-api","sl4j-simple","jcl104-over-slf4j"
   		excludes "xercesImpl","xmlParserAPIs", "servlet-api"
   		excludes "mail", "commons-lang"
   		excludes([group: "org.eclipse.jetty.orbit", name: "javax.servlet"],
           	[group: "org.eclipse.jetty.orbit", name: "javax.activation"],
           	[group: "org.eclipse.jetty.orbit", name: "javax.mail.glassfish"],
           	[group: "org.eclipse.jetty.orbit", name: "javax.transaction"])
	 }
	 // other dependencies
}

plugins {
	// other plugins
	runtime(":jetty:2.0.3") {
		excludes "jetty-http", "jetty-server", "jetty-webapp" 
		excludes "jetty-plus", "jetty-security", "jetty-websocket"
		excludes "jetty-continuation", "jetty-jndi"
	}
	// other plugins
}
```
I have also tested the plugin using the settings below:

```
dependencies {
	// other dependencies
	provided(
		"org.eclipse.jetty.aggregate:jetty-all:8.1.13.v20130916"
	) {
   		excludes "commons-el","ant", "sl4j-api","sl4j-simple","jcl104-over-slf4j"
   		excludes "xercesImpl","xmlParserAPIs", "servlet-api"
   		excludes "mail", "commons-lang"
   		excludes([group: "org.eclipse.jetty.orbit", name: "javax.servlet"],
           	[group: "org.eclipse.jetty.orbit", name: "javax.activation"],
           	[group: "org.eclipse.jetty.orbit", name: "javax.mail.glassfish"],
           	[group: "org.eclipse.jetty.orbit", name: "javax.transaction"])
	 }
	 // other dependencies
}

plugins {
	// other plugins
	runtime(":jetty:2.0.3") {
		excludes "jetty-all"
	}
	// other plugins
}
```

You should also add the following to your BuildConfig.groovy to remove the Jetty 7 jars from the war file: 

```
grails.war.resources = { stagingDir, args -> 
    delete(file: "${stagingDir}/WEB-INF/web-jetty.xml") 
    delete(file: "${stagingDir}/WEB-INF/jetty-all-7.6.0.v20120127.jar") 
} 
```

You can change the Jetty log level by adding a line to your application's grails-app/conf/Config.groovy in the appropriate place. For example, to set the level to error:

```
error "org.eclipse.jetty"
```

### Tomcat
The plugin will update your BuildConfig.groovy to use the Tomcat NIO connector if it's not already set.

```
grails.tomcat.nio = true
```

The plugin no longer uses atmosphere-runtime-native. If you prefer using it over atmosphere-runtime, insert the lines below in the dependencies section of your BuildConfig.groovy.

```
dependencies {
	compile "org.atmosphere:atmosphere-runtime-native:2.1.0", {
		excludes "slf4j-api"
	}

	// other dependencies 
}
```
You must also add ```"org.atmosphere.useWebSocketAndServlet3": "false"``` to your init params in grails-app/conf/AtmosphereMeteorConfig.groovy. See the example below.

```
defaultInitParams = [
		"org.atmosphere.useWebSocketAndServlet3": "false",
		"org.atmosphere.cpr.broadcasterCacheClass": "org.atmosphere.cache.UUIDBroadcasterCache",
		"org.atmosphere.cpr.AtmosphereInterceptor": """
			org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor,
			org.atmosphere.interceptor.HeartbeatInterceptor
		"""
]
```

See [Installing-AtmosphereServlet-with-or-without-native-support](https://github.com/Atmosphere/atmosphere/wiki/Installing-AtmosphereServlet-with-or-without-native-support) for more information.

### MeteorServlet

Create a MeteorServlet. Changes to these classes are reloaded automatically.

```
grails create-meteor-servlet com.example.Default
```

### Handler

Create a handler. Changes to these classes are reloaded automatically.

```
grails create-meteor-handler com.example.Default
```

### Servlet Configuration

Edit grails-app/conf/AtmosphereMeteorConfig.groovy. Changes to this file will be implemented when the application is restarted.

```
import com.example.DefaultMeteorHandler

defaultMapping = "/atmosphere/*"

servlets = [
    MeteorServletDefault: [
        className: "com.example.DefaultMeteorServlet",
        mapping: "/atmosphere/*",
       	handler: DefaultMeteorHandler,
 		initParams = [
			// Uncomment the line below use native WebSocket support with native Comet support.
			//"org.atmosphere.useWebSocketAndServlet3": "false",
			"org.atmosphere.cpr.broadcasterCacheClass": "org.atmosphere.cache.UUIDBroadcasterCache",
			"org.atmosphere.cpr.AtmosphereInterceptor": """
				org.atmosphere.client.TrackMessageSizeInterceptor,
				org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor,
				org.atmosphere.interceptor.HeartbeatInterceptor
			"""
		]
    ]
]
```
### Log Level

You can change the Atmosphere log level by adding a line to your application's grails-app/conf/Config.groovy in the appropriate place. For example, to set the level to warn:

```
warn "org.atmosphere"
```

### Update Atmosphere Javascript Files

You can update the Atmosphere Javascript files by running the script below. This will allow you to update the client files without having to wait on a plugin release.

```
grails update-atmosphere-meteor-javascript
```





