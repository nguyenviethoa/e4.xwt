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
package org.eclipse.e4.tools.ui.designer;

import org.eclipse.e4.tools.ui.designer.editparts.SashEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.tools.SelectionTool;

public class E4SelectionTool extends SelectionTool {

	public E4SelectionTool() {
	}

	@Override
	protected boolean updateTargetUnderMouse() {
		EditPart editPart = getTargetEditPart();
		if (editPart == null) {
			setDefaultCursor(null);
			try {
				return super.updateTargetUnderMouse();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (editPart instanceof SashEditPart) {
			SashEditPart sashEditPart = (SashEditPart) editPart;
			setDefaultCursor(sashEditPart.getDefaultCursor());
		}
		else {
			setDefaultCursor(null);
		}
		return super.updateTargetUnderMouse();
	}
}
