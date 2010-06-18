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
package org.eclipse.e4.tools.ui.designer.part;

import org.eclipse.gef.Request;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class PartReqHelper {

	public static final String WRAPPER_DATA = "PART_REQUEST_WRAPPER";

	public static PartRequest wrap(PartRequest partReq, Request host) {
		host.getExtendedData().put(WRAPPER_DATA, partReq);
		return partReq;
	}

	public static PartRequest unwrap(Request host) {
		PartRequest partReq = (PartRequest) host.getExtendedData().get(
				WRAPPER_DATA);
		return partReq;
	}
}
