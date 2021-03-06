/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.tests.attachedproperty;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AttachedPropertySuite extends TestSuite {
	public static final Test suite() {
		return new AttachedPropertySuite();
	}

	public AttachedPropertySuite() {
		addTest(new TestSuite(AttachedPropertyTests.class));
	}
}
