import org.grails.plugins.atmosphere_meteor.ApplicationContextHolder

beans = {
   applicationContextHolder(ApplicationContextHolder) { bean ->
      bean.factoryMethod = 'getInstance'
   }
}
