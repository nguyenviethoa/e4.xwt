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
package org.eclipse.e4.tools.ui.designer.editparts;

import org.eclipse.e4.tools.ui.designer.editparts.handlers.DragSashTracker;
import org.eclipse.e4.ui.model.application.MApplicationFactory;
import org.eclipse.e4.xwt.tools.ui.designer.core.visuals.IVisualInfo;
import org.eclipse.e4.xwt.tools.ui.designer.core.visuals.swt.ControlInfo;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.SharedCursors;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Sash;

/**
 * @author jin.liu (jin.liu@soyatec.com)
 * 
 */
public class SashEditPart extends ControlEditPart {

	private Sash sash;

	public SashEditPart(Sash sash, EObject model) {
		super(model);
		this.sash = sash;
		if (model == null) {
			model = (EObject) MApplicationFactory.eINSTANCE.createItem();
			setModel(model);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.tools.ui.designer.parts.ControlEditPart#createVisualInfo()
	 */
	protected IVisualInfo createVisualInfo() {
		return new ControlInfo(sash, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.editparts.AbstractGraphicalEditPart#getDragTracker(org
	 * .eclipse.gef.Request)
	 */
	public DragTracker getDragTracker(Request request) {
		return new DragSashTracker(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.tools.ui.designer.parts.WidgetEditPart#toString()
	 */
	public String toString() {
		return "Sash";
	}
	
	public Cursor getDefaultCursor() {
		if (isHorizontal()) {
			return SharedCursors.SIZENS;			
		}
		return SharedCursors.SIZEWE;			
	}
	
	@Override
	public Command getCommand(Request request) {
		if (request.getType().equals(RequestConstants.REQ_DELETE)) {
			return UnexecutableCommand.INSTANCE;
		}
		return super.getCommand(request);
	}
	
	public boolean isHorizontal() {
		return ((sash.getStyle() & SWT.HORIZONTAL) == SWT.HORIZONTAL);		
	}

}