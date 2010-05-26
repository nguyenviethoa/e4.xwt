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

import org.eclipse.e4.ui.workbench.swt.internal.PartRenderingEngine;


/**
 * This class is used to setup the Designer's render, which is useful to hide the shell in edition. 
 * 
 * @author yyang
 *
 */
public class DesignerPartRenderingEngine extends PartRenderingEngine {
	public static final String engineURI = "platform:/plugin/org.eclipse.e4.tools.ui.designer/"
		+ "org.eclipse.e4.tools.ui.designer.render.DesignerPartRenderingEngine";

	public static final String factoryURI = "platform:/plugin/org.eclipse.e4.tools.ui.designer/"
		+ "org.eclipse.e4.tools.ui.designer.render.DesignerWorkbenchRendererFactory";

	public static String defaultRenderingFactoryId = "org.eclipse.e4.tools.ui.designer.renderers.default";
	
	public DesignerPartRenderingEngine() {
		super(factoryURI);
	}
}
