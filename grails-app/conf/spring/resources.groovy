import org.grails.plugins.atmosphere_meteor.AtmosphereConfigurationHolder

beans = {
	atmosphereConfigurationHolder(AtmosphereConfigurationHolder) { bean ->
      bean.factoryMethod = 'getInstance'
   }
}
