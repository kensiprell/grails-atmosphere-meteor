package org.grails.plugins.atmosphere_meteor

import org.atmosphere.cpr.AtmosphereFramework
import org.atmosphere.cpr.Broadcaster
import org.atmosphere.cpr.DefaultBroadcaster
import org.grails.plugins.atmosphere_meteor.AtmosphereConfigurationHolder

class AtmosphereMeteorService {
	def atmosphereMeteor

	/**
	 * Broadcasts a message to a client or clients, first creating the broadcaster if it does not exist.
	 * @param mapping  a String that denotes the URL path to the client or clients
	 * @param data  can be JSON, String, or whatever your client accepts
	 */
	def broadcast(String mapping, data) {
		broadcastData(DefaultBroadcaster.class, mapping, data, true)		
    }

	/**
	 * Broadcasts a message to a client or clients.
	 * @param mapping  a String that denotes the URL path to the client or clients
	 * @param data  can be JSON, String, or whatever your client accepts
	 * @param create  a Boolean, true will create the broadcaster if it does not exist and false will not 
	 */
   def broadcast(String mapping, data, Boolean create) {
		broadcastData(DefaultBroadcaster.class, mapping, data, create)		
    }
	
	private broadcastData(clazz, String mapping, data, boolean create) {
		Broadcaster broadcaster = atmosphereMeteor.broadcasterFactory.lookup(clazz, mapping, create)
		broadcaster.broadcast(data)
	}
}
