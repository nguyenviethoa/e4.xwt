/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.       *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *                                                                             *  
 * Contributors:                                                               *        
 *     Soyatec - initial API and implementation                                *
 *******************************************************************************/
package org.eclipse.e4.xwt.dataproviders;

import java.net.URL;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public interface IXMLDataProvider extends IDataProvider {

	void setSource(URL xmlSource);

	URL getSource();

	void setXPath(String xPath);

	String getXPath();

	void setXDataContent(String content);

	String getXDataContent();
}
