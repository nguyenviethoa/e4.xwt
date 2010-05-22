/*******************************************************************************
 * Copyright (c) 2006, 2010 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.ui.designer.actions;

import org.eclipse.e4.tools.ui.designer.E4Designer;
import org.eclipse.e4.tools.ui.designer.E4DesignerPlugin;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.outline.DesignerOutlinePage;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * 
 * @author yyang <yves.yang@soyatec.com>
 *
 */
public class DesignerSelectionTools {
	public static void selectElement(Object object, EditPartViewer viewer) {
		EditPartViewer editPartViewer = viewer;
		EditPart editpart = (EditPart) editPartViewer.getEditPartRegistry().get(
				object);
		if (editpart == null) {
			IWorkbenchPart workbenchPart = E4DesignerPlugin.getDefault()
					.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().getActivePart();
			if (workbenchPart instanceof E4Designer) {
				E4Designer designer = (E4Designer) workbenchPart;
				IContentOutlinePage contentOutlinePage = (IContentOutlinePage) designer
						.getAdapter(IContentOutlinePage.class);
				if (contentOutlinePage instanceof DesignerOutlinePage) {
					DesignerOutlinePage designerOutlinePage = (DesignerOutlinePage) contentOutlinePage;
					editPartViewer = designerOutlinePage.getTreeViewer();
					editpart = (EditPart) editPartViewer.getEditPartRegistry().get(
							object);
				}
			}
		}

		if (editpart != null) {
			editPartViewer.reveal(editpart);
			editPartViewer.select(editpart);
		} else {
			StructuredSelection selection = new StructuredSelection(object);
			IWorkbenchPart workbenchPart = E4DesignerPlugin.getDefault()
					.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().getActivePart();
			ISelectionProvider selectionProvider = workbenchPart.getSite()
					.getSelectionProvider();
			if (selectionProvider != null) {
				selectionProvider.setSelection(selection);
			}
		}
	}
}
