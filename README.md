## Grails plugin for integrating the Atmosphere Framework

[Atmosphere Wiki](https://github.com/Atmosphere/atmosphere/wiki)

[Atmosphere Google Group](https://groups.google.com/forum/?fromgroups#!forum/atmosphere-framework)

The plugin has been tested in the following environment, using the [grails-atmosphere-meteor-sample](https://github.com/kensiprell/grails-atmosphere-meteor-sample) application and [grails-plugin-test-script](https://github.com/kensiprell/grails-plugin-test-script):

* atmosphere-runtime 2.2.0

* OSX 10.9.4

* JDK 1.7.0_67

* Grails versions 2.1.5, 2.2.4, 2.3.9, and 2.4.3

* Tomcat 7.0.27 through 7.0.54 (version depends on Grails version)

* Jetty plugin version 3.0.0, available in Grails 2.3.7 or greater


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

### Configuration

All servlets are defined in ```grails-app/conf/AtmosphereMeteorConfig.groovy```. A default file that you must edit will be copied to your app when the plugin is first installed.

### MeteorServlet Class

The ```create-meteor-servlet``` script creates a class in grails-app/atmosphere that extends Atmosphere's MeteorServlet. You could probably use a single class throughout your application.

Although the [sample application](https://github.com/kensiprell/grails-atmosphere-meteor-sample) uses the same MeteorServlet class for each URL, you can easily use a different class. Of course, each of the URL patterns above can be further divided using a combination of request headers, path, etc. For example, a chat room could be established under /atmosphere/chat/private-room that is serviced by the same servlet, MeteorServlet, and MeteorHandler classes as /atmosphere/chat/*.

### MeteorHandler Class

The ```create-meteor-handler``` script creates a class in grails-app/atmosphere that extends HttpServlet. This is where you customize how the HTTP requests and responses (including Atmosphere Broadcaster) are handled.

## Plugin Installation, Configuration, and Use

### Installation
Edit your BuildConfig.groovy:

```
plugins {
    // other plugins
    compile ":atmosphere-meteor:0.9.0"
    // other plugins
}
```

### Jetty

The plugin works with the Jetty plugin version 3.0.0 (Jetty 9) or greater, which can be used with Grails versions 2.3.7 and greater. See the [Jetty Page](https://github.com/kensiprell/grails-atmosphere-meteor/wiki/Jetty) for workarounds with Jetty 8.

### Tomcat 7
Your BuildConfig.groovy must be configured to use the Servlet 3.0 API and the Tomcat NIO connector.

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
 		initParams: [
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

### AtmosphereConfigurationHolder

This class is used to hold the ```AtmosphereFramework``` instance as a property and is used to return ```AtmosphereMeteorConfig.groovy``` as a ```ConfigObject```. The ```ConfigObject``` is used mostly internally; however, the lines below could be useful if you need grab the ```AtmosphereFramework``` instance somewhere in your application.

```
def atmosphereConfigurationHolder
AtmosphereFramework framework = atmosphereConfigurationHolder.framework
```

Note that this property must be set before it is available. The ```create-meteor-servlet``` script will take care of this automatically. However, if you have an existing application, add the last line below to your ```grails-app/atmosphere/DefaultMeteorServlet.groovy``` class or whatever you have named your MeteorServlet class.

```
class DefaultMeteorServlet extends MeteorServlet {
	@Override
	public void init(ServletConfig sc) throws ServletException {
		super.init(sc)
		AtmosphereConfigurationHolder.framework = framework
```

### AtmosphereMeteorService

The plugin has a service that you can inject into your application artifacts using the line below. 

```
def atmosphereMeteorService
```

#### broadcast()

The ```broadcast()``` method uses the [Atmosphere DefaultBroadcaster](http://atmosphere.github.io/atmosphere/apidocs/org/atmosphere/cpr/DefaultBroadcaster.html) and offers two signatures. The one below will create the broadcaster if it does not exist, and the second one allows you to turn off this behavior by using ```false``` as the ```create``` argument. These are convenience methods that save you from having to grab the ```BroadcasterFactory``` instance.

```
atmosphereMeteorService.broadcast(String mapping, data)
```

```
atmosphereMeteorService.broadcast(String mapping, data, Boolean create)
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






