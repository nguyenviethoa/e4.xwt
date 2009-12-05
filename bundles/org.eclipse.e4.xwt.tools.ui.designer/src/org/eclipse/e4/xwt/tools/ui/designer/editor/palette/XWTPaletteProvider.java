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
package org.eclipse.e4.xwt.tools.ui.designer.editor.palette;

import org.eclipse.e4.xwt.tools.ui.designer.XWTDesignerPlugin;
import org.eclipse.e4.xwt.tools.ui.palette.page.resources.PaletteResourceProvider;
import org.eclipse.emf.common.util.URI;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class XWTPaletteProvider extends PaletteResourceProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.tools.palette.page.resources.PaletteResourceProvider#getPaletteResourceURI()
	 */
	protected URI getPaletteResourceURI() {
		return URI.createPlatformPluginURI(XWTDesignerPlugin.PLUGIN_ID + "/palette/xwt.palette", true);
	}

}