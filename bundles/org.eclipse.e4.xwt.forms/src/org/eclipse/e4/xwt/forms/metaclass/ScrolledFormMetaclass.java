/*******************************************************************************
 * Copyright (c) 2006, 2010 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Soyatec and Erdal Karaca - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.forms.metaclass;

import org.eclipse.e4.xwt.forms.metaclass.properties.DecoratingHeading;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * 
 * @author Erdal Karaca <erdal.karaca.de@googlemail.com>
 * @author yves.yang (yves.yang@soyatec.com)
 */
public class ScrolledFormMetaclass extends AbstractFormMetaclass {
	public ScrolledFormMetaclass() {
		super(ScrolledForm.class);
		addProperty(new DecoratingHeading());
	}

	@Override
	protected Control doCreateControl(FormToolkit tk, Composite parent,
			int style) {
		return tk.createScrolledForm(parent);
	}
}
