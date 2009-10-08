/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.core;


/**
 * Setter of the class Style, which is used to define the in-line XAML style
 * 
 * @see Style
 * @author yyang
 */
public class Setter extends Setterbase {
	protected String property;
	protected String value;
	protected String tergatName;

	public String getTergatName() {
		return tergatName;
	}

	public void setTergatName(String tergatName) {
		this.tergatName = tergatName;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
