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
package org.eclipse.e4.xwt.tools.ui.designer.parts;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.tools.ui.designer.commands.ChangeWeightsCommand;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlFactory;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.swt.widgets.Sash;

/**
 * @author jin.liu (jin.liu@soyatec.com)
 * 
 */
public class SashEditPart extends ControlEditPart {

	public SashEditPart(Sash sash, XamlNode model) {
		super(sash, model);
		if (model == null) {
			model = XamlFactory.eINSTANCE.createElement("Sash",
					IConstants.XWT_NAMESPACE);
			setModel(model);
		}
	}

	public Command getCommand(Request request) {
		if (getParent() instanceof SashFormEditPart
				&& request instanceof ChangeBoundsRequest) {
			return new ChangeWeightsCommand((SashFormEditPart) getParent(),
					(ChangeBoundsRequest) request);
		}
		return super.getCommand(request);
	}

}