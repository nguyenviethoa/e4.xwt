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
package org.eclipse.e4.tools.ui.designer.editparts;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import org.eclipse.swt.widgets.ToolItem;

import org.eclipse.e4.tools.ui.designer.sashform.SashFormEditPart;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.impl.ApplicationImpl;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainer;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Widget;

/**
 * @author jin.liu(jin.liu@soyatec.com)
 */
public class E4EditPartsFactory implements EditPartFactory {

	public EditPart createEditPart(EditPart context, Object model) {
		if (model instanceof MApplication) {
			return new DiagramEditPart((ApplicationImpl) model);
		} else if (model instanceof MUIElement) {
			MUIElement elementImpl = (MUIElement) model;
			Object widget = ((MUIElement) model).getWidget();
			if (widget == null) {
				return new InvisibleEditPart(model);
			}
			if (model instanceof MPartSashContainer) {
				return new SashFormEditPart((EObject) elementImpl);
			} else if (model instanceof MPartStack) {
				return new PartContainerEditPart((EObject) elementImpl);
			}
			if (widget instanceof Shell) {
				return new ShellEditPart((EObject) elementImpl);
			} else if (widget instanceof ToolBar) {
				return new ToolBarEditPart((EObject) elementImpl);
			} else if (widget instanceof ToolItem) {
				return new ToolItemEditPart((EObject) model);
			} else if (widget instanceof SashForm) {
				return new SashFormEditPart((EObject) elementImpl);
			} else if (widget instanceof CTabFolder || widget instanceof org.eclipse.e4.ui.widgets.CTabFolder) {
				return new PartContainerEditPart((EObject) elementImpl);
			} else if (widget instanceof Composite) {
				return new CompositeEditPart((EObject) elementImpl);
			} else if (widget instanceof Control) {
				return new ControlEditPart((EObject) elementImpl);
			} else if (widget instanceof Widget) {
				return new WidgetEditPart((EObject) model);
			} else if (widget instanceof Viewer) {
				return new ViewerEditPart((EObject) elementImpl);
			}
		}
		return null;
	}

}
