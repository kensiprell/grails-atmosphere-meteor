import grails.util.Holders

import org.apache.log4j.Logger

import org.atmosphere.cpr.AtmosphereFramework
import org.atmosphere.cpr.BroadcasterFactory

class AtmosphereMeteorBootStrap {

	private static final log = Logger.getLogger("org.grails.plugins.atmosphere_meteor.conf.AtmosphereMeteorBootStrap")
	
	def atmosphereMeteor
	def atmosphereConfigurationHolder
	def grailsApplication
	
	def init = { servletContext ->
		initializeBeans()
	}
	
	void initializeBeans() {
		log.info "Initializing atmosphereMeteor bean ...."
		def config = atmosphereConfigurationHolder.pluginConfig.initializeBeans
		def delay = config.delay
		def period = config.period
		def attempts = config.attempts
		def count = 0
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
					log.info "atmosphereMeteor bean initialized"
					timer.cancel()
					timer.purge()
				} else {
					count++
					if (count > attempts) {
						log.error "AtmosphereFramework not found: atmosphereMeteor bean not initialized"
						timer.cancel()
						timer.purge()
					}
				}
			}
		}
		timer.schedule(task, delay, period)
	}
}
