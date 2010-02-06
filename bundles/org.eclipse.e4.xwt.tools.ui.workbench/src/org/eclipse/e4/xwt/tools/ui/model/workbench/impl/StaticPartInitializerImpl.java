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
package org.eclipse.e4.xwt.tools.ui.model.workbench.impl;

import org.eclipse.e4.xwt.tools.ui.model.workbench.StaticPartInitializer;
import org.eclipse.e4.xwt.tools.ui.model.workbench.WorkbenchPackage;

import org.eclipse.e4.xwt.tools.ui.palette.impl.InitializerImpl;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Static Part Initializer</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class StaticPartInitializerImpl extends InitializerImpl implements StaticPartInitializer {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected StaticPartInitializerImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return WorkbenchPackage.Literals.STATIC_PART_INITIALIZER;
	}

	@Override
	public boolean initialize(Object element) {
		return true;
	}
} //StaticPartInitializerImpl
