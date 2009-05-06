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
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;
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
	public void bind(IObservableValue source, IObservableValue target, UpdateValueStrategy targetToSource, UpdateValueStrategy sourceToTarget) {
		if (source != null && target != null) {
			this.observeValue = source;
			this.observeWidget = target;
			DataBindingContext core = new DataBindingContext(XWT.realm);

			// Add converter to UpdateValueStrategy.
			Object sourceValueType = source.getValueType();
			Object targetValueType = target.getValueType();
			Class<?> sourceType = (sourceValueType instanceof Class<?>) ? (Class<?>) sourceValueType : sourceValueType.getClass();
			Class<?> targetType = (targetValueType instanceof Class<?>) ? (Class<?>) targetValueType : targetValueType.getClass();
			if (sourceToTarget == null) {
				sourceToTarget = new UpdateValueStrategy();
			}
			IConverter m2t = XWT.findConvertor(sourceType, targetType);
			if (m2t != null) {
				sourceToTarget.setConverter(m2t);
			}
			if (targetToSource == null) {
				targetToSource = new UpdateValueStrategy();
			}
			IConverter t2m = XWT.findConvertor(targetType, sourceType);
			if (t2m != null) {
				targetToSource.setConverter(t2m);
			}

			core.bindValue(target, source, targetToSource, sourceToTarget);
		}
	}
}
