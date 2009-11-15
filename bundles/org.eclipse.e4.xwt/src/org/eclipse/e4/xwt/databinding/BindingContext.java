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

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.IBindingContext;
import org.eclipse.e4.xwt.IDataBindingInfo;
import org.eclipse.e4.xwt.IValueConverter;
import org.eclipse.e4.xwt.InverseValueConverter;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.XWTLoader;

/**
 * @author jliu jin.liu@soyatec.com
 */
public class BindingContext implements IBindingContext {

	public Binding bind(IObservableValue source, IObservableValue target) {
		return bind(source, target, null);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.databinding.IBindingContext#bind(org.eclipse.core.databinding.observable.value.IObservableValue, org.eclipse.core.databinding.observable.value.IObservableValue)
	 */
	public Binding bind(IObservableValue source, IObservableValue target, IDataBindingInfo dataBinding) {
		IValueConverter converter = null;
		int sourceToTargetPolicy = UpdateValueStrategy.POLICY_UPDATE;
		int targetToSourcePolicy = UpdateValueStrategy.POLICY_UPDATE;
		// Set policy to UpdateValueStrategy.
		if (dataBinding != null) {
			switch (dataBinding.getBindingMode()) {
			case OneWay:
				targetToSourcePolicy = UpdateValueStrategy.POLICY_NEVER;
				break;
			case OneTime:
				sourceToTargetPolicy = UpdateValueStrategy.POLICY_NEVER;
				targetToSourcePolicy = UpdateValueStrategy.POLICY_NEVER;
				break;
			default:
				break;
			}
			converter = dataBinding.getConverter();
		}
		UpdateValueStrategy sourceToTarget = new UpdateValueStrategy(sourceToTargetPolicy);
		UpdateValueStrategy targetToSource = new UpdateValueStrategy(targetToSourcePolicy);
		
		return bind(source, target, sourceToTarget, targetToSource, converter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.databinding.IBindingContext#bind(org.eclipse.core.databinding.observable.value.IObservableValue, org.eclipse.core.databinding.observable.value.IObservableValue)
	 */
	public Binding bind(IObservableValue source, IObservableValue target, UpdateValueStrategy sourceToTarget, UpdateValueStrategy targetToSource, IValueConverter converter) {
		if (converter != null) {
			return bind(source, target, sourceToTarget, targetToSource, converter, new InverseValueConverter(converter));			
		}
		else {
			return bind(source, target, sourceToTarget, targetToSource, null, null);						
		}
	}

	/**
	 * 
	 * @param source
	 * @param target
	 * @param sourceToTarget if it is null, the default converter will be update policy
	 * @param targetToSource if it is null, the default converter will be update policy
	 * @param sourceToTargetConvertor if it is null, the default converter will be used
	 * @param targetToSourceConvertor if it is null, the default converter will be used
	 */
	public Binding bind(IObservableValue source, IObservableValue target, UpdateValueStrategy sourceToTarget, UpdateValueStrategy targetToSource, 
			IConverter sourceToTargetConvertor, IConverter targetToSourceConvertor) {
		if (source != null && target != null) {
			if (sourceToTarget == null) {
				sourceToTarget = new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);
			}
			if (targetToSource == null) {
				targetToSource = new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);
			}
			
			IObservableValue observeValue = source;
			IObservableValue observeWidget = target;
			// Add converter to UpdateValueStrategy.
			Object sourceValueType = source.getValueType();
			if (sourceValueType == null) {
				sourceValueType = Object.class;
			}
			Object targetValueType = target.getValueType();
			if (targetValueType == null) {
				targetValueType = Object.class;
			}
			Class<?> sourceType = (sourceValueType instanceof Class<?>) ? (Class<?>) sourceValueType : sourceValueType.getClass();
			Class<?> targetType = (targetValueType instanceof Class<?>) ? (Class<?>) targetValueType : targetValueType.getClass();
			if (sourceType == null) {
				sourceType = Object.class;
			} else {
				sourceType = XWTLoader.normalizedType(sourceType);
			}

			if (targetType == null) {
				targetType = Object.class;
			} else {
				targetType = XWTLoader.normalizedType(targetType);
			}

			if (sourceToTargetConvertor != null) {
				sourceToTarget.setConverter(sourceToTargetConvertor);
			} else if (!targetType.isAssignableFrom(sourceType)) {
				IConverter m2t = XWT.findConvertor(sourceType, targetType);
				if (m2t != null) {
					sourceToTarget.setConverter(m2t);
				}
			}

			if (targetToSourceConvertor != null) {
				targetToSource.setConverter(targetToSourceConvertor);
			} else if (!sourceType.isAssignableFrom(targetType)) {
				IConverter t2m = XWT.findConvertor(targetType, sourceType);
				if (t2m != null) {
					targetToSource.setConverter(t2m);
				}
			}

			DataBindingContext core = new DataBindingContext(XWT.getRealm());
			return core.bindValue(target, source, targetToSource, sourceToTarget);
		}
		return null;
	}
}
