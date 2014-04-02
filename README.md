## Grails plugin for integrating the Atmosphere Framework

[Atmosphere Wiki](https://github.com/Atmosphere/atmosphere/wiki)

[Atmosphere Google Group](https://groups.google.com/forum/?fromgroups#!forum/atmosphere-framework)

The plugin has been tested in the following environment, using the [grails-atmosphere-meteor-sample](https://github.com/kensiprell/grails-atmosphere-meteor-sample) application and [grails-plugin-test-script](https://github.com/kensiprell/grails-plugin-test-script):

* atmosphere-runtime 2.1.2

* OSX 10.9.2

* JDK 1.7.0_51

* Grails versions 2.1.5, 2.2.4, and 2.3.7

* Tomcat 7.0.27 through 7.0.52 (version depends on Grails version).

* Jetty 8.1.13.v20130916


If you have a question, problem, suggestion, or want to report a bug, please submit an [issue](https://github.com/kensiprell/grails-atmosphere-meteor/issues?state=open). I will reply as soon as I can.

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

All servlets are defined in grails-app/conf/AtmosphereMeteorConfig.groovy. A default file that you must edit will be copied to your app when the plugin is first installed.

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
    compile ":atmosphere-meteor:0.8.3"
    // other plugins
}
```

### Jetty

See the [Jetty Page](https://github.com/kensiprell/grails-atmosphere-meteor/wiki/Jetty) for versions 8 and higher.

### Tomcat 7
Change your BuildConfig.groovy to use the Servlet 3.0 API and the Tomcat NIO connector.

```
grails.servlet.version = "3.0"
grails.tomcat.nio = true
```

### Tomcat 8

Search the [BuildConfig.groovy](https://github.com/kensiprell/grails-atmosphere-meteor-sample/blob/master/grails-app/conf/BuildConfig.groovy) for the sample app for "tomcat8" for configuring Tomcat 8.

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

### Javascript

The plugin supports both the asset-pipeline and resources plugins. You can use the following tags depending on whether or not you are using jQuery.

#### asset-pipeline plugin

```
<asset:javascript src="atmosphere-meteor.js"/>
```
or

```
<asset:javascript src="atmosphere-meteor-jquery.js"/>
```

You can also add the dependency in one of your JavaScript manifests:

```
//= require atmosphere-meteor
```
or

```
//= require atmosphere-meteor-jquery
```

#### resources plugin

```
<r:require module="atmosphere-meteor"/>
```
or

```
<r:require module="atmosphere-meteor-jquery"/>
```

#### Update

You can update the Atmosphere Javascript files by running the script below. This will allow you to update the client files without having to wait on a plugin release.

```
grails update-atmosphere-meteor-javascript
```

### Message Brokers

#### Hazelcast

See [Hazelcast Broadcaster](https://github.com/kensiprell/grails-atmosphere-meteor/wiki/Hazelcast-Broadcaster).

### Atmosphere Runtime Native

The plugin no longer uses atmosphere-runtime-native. If you prefer using it over atmosphere-runtime, insert the lines below in the dependencies section of your BuildConfig.groovy.

```
dependencies {
	compile "org.atmosphere:atmosphere-runtime-native:2.1.1", {
		excludes "slf4j-api"
	}

	// other dependencies 
}
```
You must also add ```"org.atmosphere.useWebSocketAndServlet3": "false"``` to your init params in grails-app/conf/AtmosphereMeteorConfig.groovy if using atmosphere-runtime-native. See the example below.

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






