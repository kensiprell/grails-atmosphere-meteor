## Grails plugin for integrating the Atmosphere Framework
https://github.com/Atmosphere/atmosphere/wiki

The plugin use the following pieces of the Atmosphere Framework:

* jquery.atmosphere.js (https://github.com/Atmosphere/atmosphere/wiki/jQuery.atmosphere.js-API)

* MeteorServlet (http://atmosphere.github.com/atmosphere/apidocs/org/atmosphere/cpr/MeteorServlet.html)


* ReflectorServletProcessor (http://atmosphere.github.com/atmosphere/apidocs/org/atmosphere/handler/ReflectorServletProcessor.html)

* DefaultBroadcaster (http://atmosphere.github.com/atmosphere/apidocs/org/atmosphere/cpr/DefaultBroadcaster.html)

* SimpleBroadcaster (http://atmosphere.github.com/atmosphere/apidocs/org/atmosphere/util/SimpleBroadcaster.html)

## How It Works

### JavaServlet

The plugin is designed to create and use a JavaServlet for each main or significant URL pattern. For example, if you install the plugin as a standalone application, you will see that a JavaServlet is created for each URL pattern below:

	/jabber/chat/*

	/jabber/notification/*

	/jabber/public/*

The servlets are created programmatically using .addServlet and are not described in web.xml. Changes in development mode are reloaded automatically.

### Configuration

The configuration file, grails-app/conf/Atmosphere2Config.groovy, is used to tie the MeteorServlet and MeteorHandler classes together.

### MeteorServlet Class

The create-meteor-servlet script creates a class in grails-app/atmosphere that extends Atmosphere's MeteorServlet. You could probably use a single class throughout your application.

Although the application uses the same MeteorServlet class for each URL, you can easily use a different class. Of course, each of the URL patterns above can be further divided using a combination of request headers, Broadcaster, etc. For example, a chat room could be established under /jabber/chat/private-room/* that is serviced by the same JavaServlet and handler classes as /jabber/chat/*.

### MeteorHandler Class

The create-meteor-handler script creates a class class in grails-app/atmosphere that extends HttpServlet. This is where you customize how the incoming HTTP requests are handled and how the outgoing requests and the broadcasting functions.
The plugin source can be downloaded and used as a standalone Grails application. I suggest running it first before installing the plugin and reviewing the files below to understand how it all works. Note that many of the files are not packaged into the finished plugin.

* grails-app/atmosphere/org/grails/plugins/atmosphere2/DefaultMeteorHandler.groovy

* grails-app/atmosphere/org/grails/plugins/atmosphere2/DefaultMeteorServlet.groovy

* grails-app/conf/Atmosphere2Config.groovy

* grails-app/controllers/org/grails/plugins/atmosphere2/AtmosphereTestController.groovy

* grails-app/services/org/grails/plugins/atmosphere2/AtmosphereTestService.groovy

* grails-app/views/AtmosphereTest/index.gsp: This file contains all internal JavaScript.

* src/groovy/org/grails/plugins/atmosphere2/ApplicationContextHolder (Burt Beckwith)

## Standalone Application Installation

1. Clone and extract the repository

2. cd /path/to/grails-atmosphere2

3. grails run-app

You will have a simple application that performs the following tasks out of the box. Please note that this sample is not production ready. It merely incorporates some of the lessons I have learned and provides a point of departure for your own application.

* Chat (open two different browsers on your computer and start chatting)

* One-time triggered notification

* Automatically updates the web page at predefined intervals

## Plugin Installation

The instructions assume you are using Tomcat as the servlet container.

1. cd /path/to/your/application

2. grails install-plugin atmosphere2

3. Create a MeteorServlet:
    grails create-meteor-servlet com.example.Default

4. Create a handler:
    grails create-meteor-handler com.example.Default

5. Edit grails-app/conf/Atmosphere2Config.groovy
```groovy
    import com.example.DefaultMeteorHandler

    defaultMapping = "/jabber/*"

    servlets = [
        MeteorServlet: [
        description: "MeteorServlet Default",
        className: "com.example.DefaultMeteorServlet",
        mapping: "/jabber*/",
        handler: DefaultMeteorHandler
        ]
    ]
```
6. Note the changes the plugin installation made to grails-app/conf/BuildConfig.groovy
```groovy
    grails.servlet.version = "3.0"
    grails.tomcat.nio = true

    grails.project.dependency.resolution = {
        dependencies {
            compile('org.atmosphere:atmosphere-runtime:1.0.9') {
                excludes 'slf4j-api', 'atmosphere-ping'
            }
        }
    }
```

7. Use the JavaScript code in grails-app/views/atmosphereTest/index.gsp to get you started with your own client implementation.

## To Do

* Write the _Uninstall.groovy script

  * Items not changed by the user are deleted or restored to their original condition

  * Provide instructions for completely removing the plugin

* Provide a generic class that implements javax.servlet.http.HttpSessionListener to clean-up AtmosphereResource and Broadcaster resources when a user session ends

Your comments, questions, and suggestions are very welcome!

