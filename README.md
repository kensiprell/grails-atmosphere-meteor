## Grails plugin for integrating the Atmosphere Framework

[Atmosphere Wiki](https://github.com/Atmosphere/atmosphere/wiki)

[Atmosphere Google Group](https://groups.google.com/forum/?fromgroups#!forum/atmosphere-framework)

The plugin has been tested in the following environment, using the [grails-atmosphere-meteor-sample](https://github.com/kensiprell/grails-atmosphere-meteor-sample) application and [grails-plugin-test-script](https://github.com/kensiprell/grails-plugin-test-script):

* atmosphere-runtime 2.2.1

* OSX 10.10

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

The ```create-meteor-servlet``` script creates a class in grails-app/atmosphere that extends Atmosphere's MeteorServlet. You can probably use a single class throughout your application.

### MeteorHandler Class

The ```create-meteor-handler``` script creates a class in grails-app/atmosphere that extends HttpServlet. This is where you customize how the HTTP requests and responses are handled by overriding the ```doGet()``` and ```doPost()``` methods.

Each URL pattern can be differentiated using a combination of request headers, path, etc. For example, a chat room could be established under ```/atmosphere/chat/private-room-12345``` that is serviced by the same MeteorServlet and MeteorHandler classes as ```/atmosphere/chat/*```.


## Plugin Installation, Configuration, and Use

### Installation
Edit your BuildConfig.groovy:

```
plugins {
    // other plugins
    compile ":atmosphere-meteor:1.0.2"
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

Edit ```grails-app/conf/AtmosphereMeteorConfig.groovy```. Changes to this file will be implemented when the application is restarted.

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

### AtmosphereMeteor Bean

You can access the [AtmosphereFramework](http://atmosphere.github.io/atmosphere/apidocs/org/atmosphere/cpr/AtmosphereFramework.html) and default [BroadcasterFactory](http://atmosphere.github.io/atmosphere/apidocs/org/atmosphere/cpr/BroadcasterFactory.html) instances by injecting the  ```atmosphereMeteor``` bean anywhere in your application using either the standard DI procedure 

```
def atmosphereMeteor
```
or for example in a class in ```grails-app/atmosphere```

```
def atmosphereMeteor = grails.util.Holders.applicationContext.getBean("atmosphereMeteor")
```
and then accessing its properties

```
def atmosphereFramework = atmosphereMeteor.framework
def broadcasterFactory = atmosphereMeteor.broadcasterFactory
```

#### AtmosphereConfigurationHolder.framework Deprecated

I have already deprecated the ```AtmosphereConfigurationHolder.framework``` property that I made available in version 0.9.2. It is no longer necessary due to the atmosphereMeteor bean. 

If you have added the last line below in your ```MeteorServlet``` class, you can safely delete it.

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

### Logging

You can change the Atmosphere log level by adding a line to your application's ```grails-app/conf/Config.groovy``` in the appropriate place. For example, to set the level to warn:

```
warn "org.atmosphere"
```

You can change the plugin log level by adding a a line to your application's ```grails-app/conf/Config.groovy``` in the appropriate place. For example, to set the level to debug:

```
debug "org.grails.plugins.atmosphere_meteor"
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






