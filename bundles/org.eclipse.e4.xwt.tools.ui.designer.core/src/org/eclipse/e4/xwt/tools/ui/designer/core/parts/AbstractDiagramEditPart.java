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
package org.eclipse.e4.xwt.tools.ui.designer.core.parts;

import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.e4.xwt.tools.ui.designer.core.layouts.FreeformXYFlowLayout;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlDocument;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlElement;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

/**
 * @author jliu jin.liu@soyatec.com
 */
public abstract class AbstractDiagramEditPart extends AbstractGraphicalEditPart {

	public AbstractDiagramEditPart(XamlDocument document) {
		setModel(document);
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		IFigure contentPane = new FreeformLayer();
		contentPane.setBorder(new MarginBorder(20));
		contentPane.setLayoutManager(new FreeformXYFlowLayout());
		// contentPane.setBackgroundColor(ColorConstants.white);
		contentPane.setOpaque(false);
		return contentPane;
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	protected List getModelChildren() {
		if (getModel() == null) {
			return super.getModelChildren();
		}
		XamlElement rootElement = ((XamlDocument) getModel()).getRootElement();
		if (rootElement != null) {
			return Collections.singletonList(rootElement);
		}
		return super.getModelChildren();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class key) {
		if (key == SnapToHelper.class) {
			return new SnapToGeometry(this);
		}
		return super.getAdapter(key);
	}

}