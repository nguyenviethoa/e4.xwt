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

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.swt.widgets.Widget;

/**
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public interface IDynamicBinding extends IBinding {
	IObservableValue createBoundSource();

	void setControl(Widget control);

	Widget getControl();

	void setType(String type);

	String getType();

	boolean isControlSource();
}