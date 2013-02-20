package org.grails.plugins.atmosphere_meteor;

import org.codehaus.groovy.grails.commons.AbstractGrailsClass;
import org.codehaus.groovy.grails.commons.ArtefactHandlerAdapter;
import org.codehaus.groovy.grails.commons.GrailsClass;
import org.codehaus.groovy.grails.commons.GrailsClassUtils;

public class MeteorServletArtefactHandler extends ArtefactHandlerAdapter {

	public static final String TYPE = "MeteorServlet";

	public static final String SUFFIX = "MeteorServlet";

	public static interface MeteorServletClass extends GrailsClass {}

	public static class DefaultMeteorServletClass extends AbstractGrailsClass implements MeteorServletClass {

		public DefaultMeteorServletClass(Class<?> clazz) {
			super(clazz, MeteorServletArtefactHandler.SUFFIX);
		}

		public String getKey() {
			Object key = GrailsClassUtils.getStaticPropertyValue(getClazz(), "key");
			if (key == null) {
				return null;
			} else {
				return key.toString();
			}
		}
	}

	public MeteorServletArtefactHandler() {
		super(TYPE, MeteorServletClass.class, DefaultMeteorServletClass.class, SUFFIX);
	}
}
