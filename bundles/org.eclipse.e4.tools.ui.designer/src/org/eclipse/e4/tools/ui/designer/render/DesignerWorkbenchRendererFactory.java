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

import org.eclipse.e4.ui.internal.workbench.swt.AbstractPartRenderer;

import org.eclipse.e4.ui.workbench.renderers.swt.WorkbenchRendererFactory;

import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;

public class DesignerWorkbenchRendererFactory extends WorkbenchRendererFactory {
	private DesignerRender designerWindowRender;

	public AbstractPartRenderer getRenderer(MUIElement uiElement, Object parent) {
		if (uiElement instanceof MWindow) {
			if (designerWindowRender == null) {
				designerWindowRender = new DesignerRender();
				initRenderer(designerWindowRender);
			}
			return designerWindowRender;
		}
		return super.getRenderer(uiElement, parent);
	}
}
