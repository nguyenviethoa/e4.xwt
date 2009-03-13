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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public abstract class AbstractDataBinding {
	private Object source;
	private Widget target;
	private String path;

	private IObservableValue observableSource;

	private IObservableValue observableWidget;

	public AbstractDataBinding(Object source, Widget target, String path) {
		assert source != null;
		assert target != null;
		assert path != null;
		this.source = source;
		this.target = target;
		this.path = path;
	}

	public Object getSource() {
		return source;
	}

	public void setSource(Object source) {
		this.source = source;
	}

	public Widget getTarget() {
		return target;
	}

	public void setTarget(Widget target) {
		this.target = target;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Object getValue() {
		IObservableValue observableSource = getObservableSource();
		IObservableValue observableWidget = getObservableWidget();
		if (observableSource != null && observableWidget != null) {
			DataBindingContext context = new DataBindingContext();
			context.bindValue(observableWidget, observableSource, null, null);
			return observableSource.getValue();
		}
		return null;
	}

	/**
	 * @return
	 */
	protected IObservableValue createObservableWidget() {
		if (target instanceof Text)
			return SWTObservables.observeText((Text) target, SWT.Modify);
		if (target instanceof Label)
			return SWTObservables.observeText((Label) target);
		if (target instanceof Combo)
			return SWTObservables.observeText((Combo) target);
		return null;
	}

	/**
	 * @return
	 */
	protected abstract IObservableValue createObservableSource();

	public IObservableValue getObservableSource() {
		if (observableSource == null) {
			observableSource = createObservableSource();
		}
		return observableSource;
	}

	public IObservableValue getObservableWidget() {
		if (observableWidget == null) {
			observableWidget = createObservableWidget();
		}
		return observableWidget;
	}
}
