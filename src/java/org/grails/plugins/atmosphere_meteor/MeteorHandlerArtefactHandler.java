package org.grails.plugins.atmosphere_meteor;

import org.codehaus.groovy.grails.commons.AbstractGrailsClass;
import org.codehaus.groovy.grails.commons.ArtefactHandlerAdapter;
import org.codehaus.groovy.grails.commons.GrailsClass;
import org.codehaus.groovy.grails.commons.GrailsClassUtils;

public class MeteorHandlerArtefactHandler extends ArtefactHandlerAdapter {

	public static final String TYPE = "MeteorHandler";

	public static final String SUFFIX = "MeteorHandler";

	public static interface MeteorHandlerClass extends GrailsClass {}

	public static class DefaultMeteorHandlerClass extends AbstractGrailsClass implements MeteorHandlerClass {

		public DefaultMeteorHandlerClass(Class<?> clazz) {
			super(clazz, MeteorHandlerArtefactHandler.SUFFIX);
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

	public MeteorHandlerArtefactHandler() {
		super(TYPE, MeteorHandlerClass.class, DefaultMeteorHandlerClass.class, SUFFIX);
	}
}
