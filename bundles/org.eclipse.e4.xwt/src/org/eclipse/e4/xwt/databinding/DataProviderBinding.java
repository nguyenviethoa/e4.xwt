/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.       *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *                                                                             *  
 * Contributors:                                                               *        
 *     Soyatec - initial API and implementation                                *
 *******************************************************************************/
package org.eclipse.e4.xwt.databinding;

import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.dataproviders.IDataProvider;
import org.eclipse.swt.widgets.Widget;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class DataProviderBinding extends AbstractDataBinding {

	/**
	 * @param dataProvider
	 * @param target
	 * @param xPath
	 */
	public DataProviderBinding(IDataProvider dataProvider, Widget target, String xPath) {
		super(dataProvider, target, xPath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.databinding.DataBinding#createObservableSource()
	 */
	protected IObservableValue createObservableSource() {
		return new ObservableValue();
	}

	class ObservableValue extends AbstractObservableValue {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.databinding.observable.value.AbstractObservableValue#doGetValue()
		 */
		protected Object doGetValue() {
			IDataProvider source = (IDataProvider) getSource();
			return source.getData(getPath());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.databinding.observable.value.IObservableValue#getValueType()
		 */
		public Object getValueType() {
			// TODO decide the type of value.
			return String.class;
		}

	}
}
