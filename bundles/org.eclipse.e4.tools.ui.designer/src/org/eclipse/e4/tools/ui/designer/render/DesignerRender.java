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

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.e4.core.services.Logger;
import org.eclipse.e4.core.services.context.IEclipseContext;
import org.eclipse.e4.core.services.context.spi.IContextConstants;
import org.eclipse.e4.ui.model.application.MUIElement;
import org.eclipse.e4.ui.model.application.MWindow;
import org.eclipse.e4.workbench.ui.internal.Workbench;
import org.eclipse.e4.workbench.ui.renderers.swt.TrimmedPartLayout;
import org.eclipse.e4.workbench.ui.renderers.swt.WBWRenderer;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

public class DesignerRender extends WBWRenderer {
	public Object createWidget(MUIElement element, Object parent) {
		final Widget newWidget;

		if (!(element instanceof MWindow)
				|| (parent != null && !(parent instanceof Shell)))
			return null;

		MWindow wbwModel = (MWindow) element;

		Shell parentShell = (Shell) parent;

		IEclipseContext parentContext = getContextForParent(element);
		Shell wbwShell;
		if (parentShell == null) {
			wbwShell = new Shell(Display.getCurrent(), SWT.SHELL_TRIM);
		} else {
			wbwShell = new Shell(parentShell, SWT.SHELL_TRIM);
			wbwShell.setLocation(wbwModel.getX(), wbwModel.getY());
			wbwShell.setSize(wbwModel.getWidth(), wbwModel.getHeight());
		}
		wbwShell.setAlpha(0);
		wbwShell.setVisible(true);

		wbwShell.setLayout(new FillLayout());
		newWidget = wbwShell;
		bindWidget(element, newWidget);

		// set up context
		IEclipseContext localContext = getContext(wbwModel);
		localContext.set(IContextConstants.DEBUG_STRING, "MWindow"); //$NON-NLS-1$
		parentContext.set(IContextConstants.ACTIVE_CHILD, localContext);

		// Add the shell into the WBW's context
		localContext.set(Shell.class.getName(), wbwShell);
		localContext.set(Workbench.LOCAL_ACTIVE_SHELL, wbwShell);

		if (element instanceof MWindow) {
			TrimmedPartLayout tl = new TrimmedPartLayout(wbwShell);
			wbwShell.setLayout(tl);
		} else {
			wbwShell.setLayout(new FillLayout());
		}
		if (wbwModel.getLabel() != null)
			wbwShell.setText(wbwModel.getLabel());
		String uri = wbwModel.getIconURI();
		if (uri != null) {
			try {
				Image image = ImageDescriptor.createFromURL(new URL(uri))
						.createImage();
				wbwShell.setImage(image);
			} catch (MalformedURLException e) {
				// invalid image in model, so don't set an image
				Logger logger = (Logger) localContext.get(Logger.class.getName());
				logger.error(e);
			}
		}

		return newWidget;
	}
	
}
