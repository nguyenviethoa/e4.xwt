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
package org.eclipse.e4.xwt.databinding;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.XWT;

/**
 * @author jliu jin.liu@soyatec.com
 */
public class BindingContext implements IBindingContext {

	public IObservableValue observeValue;
	public IObservableValue observeWidget;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.databinding.IBindingContext#bind(org.eclipse.core.databinding.observable.value.IObservableValue, org.eclipse.core.databinding.observable.value.IObservableValue)
	 */
	public void bind(IObservableValue source, IObservableValue target) {
		if (source != null && target != null) {
			this.observeValue = source;
			this.observeWidget = target;
			DataBindingContext core = new DataBindingContext(XWT.realm);
			core.bindValue(target, source, null, null);
		}
	}

}
