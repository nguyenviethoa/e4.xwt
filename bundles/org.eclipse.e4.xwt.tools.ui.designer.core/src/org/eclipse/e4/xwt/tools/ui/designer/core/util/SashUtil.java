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
package org.eclipse.e4.xwt.tools.ui.designer.core.util;

public class SashUtil {

	public static String weightsValue(int[] weights) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < weights.length; i++) {
			if (i != 0) {
				stringBuilder.append(",");				
			}
			stringBuilder.append(weights[i]);
		}
		return stringBuilder.toString();
	}

	public static String weightsDisplayString(int[] weights) {
		return "[" + weightsValue(weights) + "]";
	}
}

