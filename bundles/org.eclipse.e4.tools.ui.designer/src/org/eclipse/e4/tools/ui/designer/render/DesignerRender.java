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
package org.eclipse.e4.tools.ui.designer.render;

import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.workbench.ui.renderers.swt.WBWRenderer;
import org.eclipse.swt.widgets.Shell;

public class DesignerRender extends WBWRenderer {

	public Object createWidget(MUIElement element, Object parent) {
		Object newWidget = super.createWidget(element, parent);
		if (newWidget != null && newWidget instanceof Shell) {
			((Shell) newWidget).setAlpha(0);
		}
		return newWidget;
	}

}
