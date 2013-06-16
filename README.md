## Grails plugin for integrating the Atmosphere Framework

[Atmosphere Wiki](https://github.com/Atmosphere/atmosphere/wiki)

The plugin has been tested on Grails versions 2.0.0 through 2.2.2. I use the [grails-atmosphere-meteor-sample](https://github.com/kensiprell/grails-atmosphere-meteor-sample) application and [grails-plugin-test-script](https://github.com/kensiprell/grails-plugin-test-script) for testing.

If you have a question, problem, suggestion, or want to report a bug, please submit an [issue](https://github.com/kensiprell/grails-atmosphere-meteor/issues?state=open). I will reply as soon as I can.

[Planned Changes](https://github.com/kensiprell/grails-atmosphere-meteor/wiki/Planned-Changes)

[Release Notes](https://github.com/kensiprell/grails-atmosphere-meteor/wiki/Release-Notes)

The plugin uses the following pieces of the Atmosphere Framework:

* [jquery.atmosphere.js](https://github.com/Atmosphere/atmosphere/wiki/jQuery.atmosphere.js-API)

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

The servlets are created programmatically using ServletContext.addServlet and are not defined in web.xml.

### Configuration

The configuration file, grails-app/conf/AtmosphereMeteorConfig.groovy, is used to tie the MeteorServlet and MeteorHandler classes together.

### MeteorServlet Class

The create-meteor-servlet script creates a class in grails-app/atmosphere that extends Atmosphere's MeteorServlet. You could probably use a single class throughout your application.

Although the [sample application](https://github.com/kensiprell/grails-atmosphere-meteor-sample) uses the same MeteorServlet class for each URL, you can easily use a different class. Of course, each of the URL patterns above can be further divided using a combination of request headers, Broadcaster, etc. For example, a chat room could be established under /jabber/chat/private-room that is serviced by the same servlet, MeteorServlet, and MeteorHandler classes as /jabber/chat/*.

### MeteorHandler Class

The create-meteor-handler script creates a class in grails-app/atmosphere that extends HttpServlet. This is where you customize how the incoming and outgoing HTTP requests (including Atmosphere Broadcaster) are handled.

## Plugin Installation

The instructions assume you are using Tomcat as the servlet container. The plugin was tested with Grails 2.2 and Tomcat 7.0.30 in a new application using Chrome 25, Firefox 19, and Safari 6 on Mountain Lion.

Edit your BuildConfig.groovy:

```
plugins {
    // other plugins
    compile ":atmosphere-meteor:0.5.1"
    // other plugins
}
```

   or use the deprecated:
    
```
grails install-plugin atmosphere-meteor
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
    MeteorServlet: [
        description: "MeteorServlet Default",
        className: "com.example.DefaultMeteorServlet",
        mapping: "/jabber/*",
        handler: DefaultMeteorHandler
    ]
]
```

Note the changes the plugin installation made to grails-app/conf/BuildConfig.groovy.

```
grails.servlet.version = "3.0"
grails.tomcat.nio = true

grails.project.dependency.resolution = {
    dependencies {
        compile('org.atmosphere:atmosphere-runtime:1.1.RC4') {
            excludes 'slf4j-api', 'atmosphere-ping'
        }
    }
}
```



