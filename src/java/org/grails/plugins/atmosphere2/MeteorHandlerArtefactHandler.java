/*
 * Copyright (c) 2013. the original author or authors:
 *
 *    Ken Siprell (ken.siprell@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.grails.plugins.atmosphere2;

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
