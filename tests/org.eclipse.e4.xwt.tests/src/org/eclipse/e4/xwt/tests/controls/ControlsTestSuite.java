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
package org.eclipse.e4.xwt.tests.controls;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.e4.xwt.tests.controls.button.ButtonTests;
import org.eclipse.e4.xwt.tests.controls.ccombo.CComboTests;
import org.eclipse.e4.xwt.tests.controls.combo.ComboTests;
import org.eclipse.e4.xwt.tests.controls.shell.ShellTests;
import org.eclipse.e4.xwt.tests.controls.tree.TreeTests;

public class ControlsTestSuite extends TestSuite {
	public static final Test suite() {
		return new ControlsTestSuite();
	}

	public ControlsTestSuite() {
		addTest(new TestSuite(ControlsTests.class));
		addTest(new TestSuite(ButtonTests.class));
		addTest(new TestSuite(ComboTests.class));
		addTest(new TestSuite(CComboTests.class));
		addTest(new TestSuite(ShellTests.class));
		addTest(new TestSuite(TreeTests.class));
	}
}
