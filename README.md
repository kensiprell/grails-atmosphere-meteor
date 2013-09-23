## Grails plugin for integrating the Atmosphere Framework

[Atmosphere Wiki](https://github.com/Atmosphere/atmosphere/wiki)

The plugin has been tested on Grails versions 2.0.0 through 2.3.0. I use the [grails-atmosphere-meteor-sample](https://github.com/kensiprell/grails-atmosphere-meteor-sample) application and [grails-plugin-test-script](https://github.com/kensiprell/grails-plugin-test-script) for testing.

If you have a question, problem, suggestion, or want to report a bug, please submit an [issue](https://github.com/kensiprell/grails-atmosphere-meteor/issues?state=open). I will reply as soon as I can.

[Planned Changes](https://github.com/kensiprell/grails-atmosphere-meteor/wiki/Planned-Changes)

[Release Notes](https://github.com/kensiprell/grails-atmosphere-meteor/wiki/Release-Notes)

The plugin uses the following pieces of the Atmosphere Framework:

* [atmosphere.js / jquery.atmosphere.js](https://github.com/Atmosphere/atmosphere-javascript)

* [MeteorServlet](http://atmosphere.github.com/atmosphere/apidocs/org/atmosphere/cpr/MeteorServlet.html)

* [ReflectorServletProcessor](http://atmosphere.github.com/atmosphere/apidocs/org/atmosphere/handler/ReflectorServletProcessor.html)

* [DefaultBroadcaster](http://atmosphere.github.com/atmosphere/apidocs/org/atmosphere/cpr/DefaultBroadcaster.html)

* [SimpleBroadcaster](http://atmosphere.github.com/atmosphere/apidocs/org/atmosphere/util/SimpleBroadcaster.html)

## How It Works

### Java Servlet

The plugin is designed to create and use a servlet for each main or significant URL pattern. For example, if you download the [sample application](https://github.com/kensiprell/grails-atmosphere-meteor-sample), you will see that a servlet is created for each URL pattern below:

	/jabber/chat/*

	/jabber/notification/*

	/jabber/public/*

The servlets are registered programmatically and are not defined in web.xml.

### Configuration

All servlets are defined in grails-app/conf/AtmosphereMeteorConfig.groovy.

 grails-app/conf/AtmosphereResources.groovy has modules for atmosphere.js and jquery.atmosphere.js.

### MeteorServlet Class

The create-meteor-servlet script creates a class in grails-app/atmosphere that extends Atmosphere's MeteorServlet. You could probably use a single class throughout your application.

Although the [sample application](https://github.com/kensiprell/grails-atmosphere-meteor-sample) uses the same MeteorServlet class for each URL, you can easily use a different class. Of course, each of the URL patterns above can be further divided using a combination of request headers, path, etc. For example, a chat room could be established under /jabber/chat/private-room that is serviced by the same servlet, MeteorServlet, and MeteorHandler classes as /jabber/chat/*.

### MeteorHandler Class

The create-meteor-handler script creates a class in grails-app/atmosphere that extends HttpServlet. This is where you customize how the HTTP requests and responses (including Atmosphere Broadcaster) are handled.

## Plugin Installation

The instructions assume you are using Tomcat as the servlet container. 

Edit your BuildConfig.groovy:

```
plugins {
    // other plugins
    compile ":atmosphere-meteor:0.6.0"
    // other plugins
}
```

Create a MeteorServlet. Changes to these classes are reloaded automatically.

```
grails create-meteor-servlet com.example.Default
```

Create a handler. Changes to these classes are reloaded automatically.

```
grails create-meteor-handler com.example.Default
```

Edit grails-app/conf/AtmosphereMeteorConfig.groovy. Changes to this file will be implemented when the application is restarted.

```
import com.example.DefaultMeteorHandler

defaultMapping = "/jabber/*"

servlets = [
    MeteorServletDefault: [
        className: "com.example.DefaultMeteorServlet",
        mapping: "/jabber/*",
       	handler: DefaultMeteorHandler,
 		initParams = [
\			"org.atmosphere.cpr.broadcasterCacheClass": "org.atmosphere.cache.UUIDBroadcasterCache",
			"org.atmosphere.cpr.AtmosphereInterceptor": """
				org.atmosphere.client.TrackMessageSizeInterceptor,
				org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor,
				org.atmosphere.interceptor.HeartbeatInterceptor
			"""
		]
    ]
]
```

Note the changes the plugin installation made to grails-app/conf/BuildConfig.groovy. Your original file was copied to grails-app/conf/BuildConfig_ORIG.groovy. 

```
grails.servlet.version = "3.0"
grails.tomcat.nio = true
```

You can change the dependency versions your application uses by editing your application's grails-app/conf/BuildConfig.groovy. This will override the versions defined in the plugin.

```
grails.project.dependency.resolution = {
    dependencies {
    	// other dependencies
        compile('org.atmosphere:atmosphere-runtime:2.0.0') {
            excludes 'slf4j-api'
        }
        compile 'org.codehaus.jackson:jackson-core-asl:1.9.13'
    	// other dependencies
    }
}
```



