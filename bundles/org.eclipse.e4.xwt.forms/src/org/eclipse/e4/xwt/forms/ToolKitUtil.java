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
package org.eclipse.e4.xwt.forms;

import org.eclipse.e4.xwt.forms.metaclass.FormMetaclass;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * 
 * @author yyang (yves.yang@soyaetc.com)
 */
public class ToolKitUtil {
	private static final String FORM_SIGNATURE_KEY = XWTForms.class.getName();

	static public synchronized FormToolkit getToolkit(Control c) {
		FormToolkit tk = findToolkit(c);
		if (tk == null) {
			tk = new FormToolkit(c.getDisplay());
			c.getDisplay().setData(FormMetaclass.class.getName(), tk);
		}
		return tk;
	}

	static public synchronized FormToolkit findToolkit(Control c) {
		return (FormToolkit) c.getDisplay().getData(
				FormMetaclass.class.getName());
	}
	
	public static void tagForm(Control control) {
		control.setData(FORM_SIGNATURE_KEY, true);
	}
	
	static public void adapt(Control control, FormToolkit toolkit) {
		Object value = control.getData(FORM_SIGNATURE_KEY);
		Composite composite = null;
		if (control instanceof Composite) {
			composite = (Composite) control;
		}
		if (toolkit != null) {
			if (composite != null) {
				toolkit.adapt(composite);					
			}
			toolkit.adapt(control, true, true);
		}
		if (value != null) {			
			toolkit = getToolkit(control);
		}
		if (composite != null) {
			for (Control child : composite.getChildren()) {
				adapt(child, toolkit);
			}
		}
	}
}
