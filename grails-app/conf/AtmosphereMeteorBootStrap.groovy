import grails.util.Holders

import org.atmosphere.cpr.AtmosphereFramework
import org.atmosphere.cpr.BroadcasterFactory

class AtmosphereMeteorBootStrap {
	def atmosphereMeteor
	
	def init = { servletContext ->
		initializeBeans()
	}
	
	void initializeBeans() {
		def timer = new Timer()
		def task = new TimerTask() {
			@Override
			 public void run() {
				 def servletContext = Holders.servletContext
				 if (servletContext.getAttribute("org.atmosphere.cpr.BroadcasterFactory")) {
					 BroadcasterFactory broadcasterFactory = servletContext.getAttribute("org.atmosphere.cpr.BroadcasterFactory")
					 AtmosphereFramework framework = broadcasterFactory.config.framework
					 atmosphereMeteor.framework = framework
					 atmosphereMeteor.broadcasterFactory = broadcasterFactory
					 timer.cancel()
					 timer.purge()
				 }
			}
		}
		timer.schedule(task, 1000, 1000)
	}
}
