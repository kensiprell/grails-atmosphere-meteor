import org.grails.plugins.atmosphere2.ApplicationContextHolder

beans = {
   applicationContextHolder(ApplicationContextHolder) { bean ->
      bean.factoryMethod = 'getInstance'
   }
}
