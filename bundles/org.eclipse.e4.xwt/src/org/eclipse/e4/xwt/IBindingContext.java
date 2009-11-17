/*******************************************************************************
 * Copyright (c) 2006, 2009 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.value.IObservableValue;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public interface IBindingContext {
	/**
	 * 
	 * @param source
	 * @param target
	 */
	Binding bind(IObservableValue source, IObservableValue target);
	
	/**
	 * 
	 * @param source
	 * @param target
	 * @param binding
	 */
	Binding bind(IObservable source, IObservable target,
			IDataBindingInfo binding);

	/**
	 * 
	 * @param source
	 * @param target
	 * @param sourceToTarget if it is null, the default converter will be update policy
	 * @param targetToSource if it is null, the default converter will be update policy
	 * @param converter
	 */
	Binding bind(IObservableValue source, IObservableValue target,
			UpdateValueStrategy sourceToTarget,
			UpdateValueStrategy targetToSource, IValueConverter converter);
	
	/**
	 * Setup the binding
	 * 
	 * @param source
	 * @param target
	 * @param sourceToTarget if it is null, the default converter will be update policy
	 * @param targetToSource if it is null, the default converter will be update policy
	 * @param sourceToTargetConvertor if it is null, the default converter will be used
	 * @param targetToSourceConvertor if it is null, the default converter will be used
	 */
	Binding bind(IObservableValue source, IObservableValue target, UpdateValueStrategy sourceToTarget, UpdateValueStrategy targetToSource, 
			IConverter sourceToTargetConvertor, IConverter targetToSourceConvertor);

}
