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
package org.eclipse.e4.xwt.tools.ui.model.workbench;

import org.eclipse.e4.xwt.tools.ui.palette.PalettePackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.e4.xwt.tools.ui.model.workbench.WorkbenchFactory
 * @model kind="package"
 * @generated
 */
public interface WorkbenchPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "workbench";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.eclipse.org/e4/xwt/workbench/desiger";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "wb";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	WorkbenchPackage eINSTANCE = org.eclipse.e4.xwt.tools.ui.model.workbench.impl.WorkbenchPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.e4.xwt.tools.ui.model.workbench.impl.StaticPartInitializerImpl <em>Static Part Initializer</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.e4.xwt.tools.ui.model.workbench.impl.StaticPartInitializerImpl
	 * @see org.eclipse.e4.xwt.tools.ui.model.workbench.impl.WorkbenchPackageImpl#getStaticPartInitializer()
	 * @generated
	 */
	int STATIC_PART_INITIALIZER = 0;

	/**
	 * The number of structural features of the '<em>Static Part Initializer</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STATIC_PART_INITIALIZER_FEATURE_COUNT = PalettePackage.INITIALIZER_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.e4.xwt.tools.ui.model.workbench.impl.DynamicPartInitializerImpl <em>Dynamic Part Initializer</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.e4.xwt.tools.ui.model.workbench.impl.DynamicPartInitializerImpl
	 * @see org.eclipse.e4.xwt.tools.ui.model.workbench.impl.WorkbenchPackageImpl#getDynamicPartInitializer()
	 * @generated
	 */
	int DYNAMIC_PART_INITIALIZER = 1;

	/**
	 * The number of structural features of the '<em>Dynamic Part Initializer</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DYNAMIC_PART_INITIALIZER_FEATURE_COUNT = PalettePackage.INITIALIZER_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.e4.xwt.tools.ui.model.workbench.impl.EditorPartInitializerImpl <em>Editor Part Initializer</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.e4.xwt.tools.ui.model.workbench.impl.EditorPartInitializerImpl
	 * @see org.eclipse.e4.xwt.tools.ui.model.workbench.impl.WorkbenchPackageImpl#getEditorPartInitializer()
	 * @generated
	 */
	int EDITOR_PART_INITIALIZER = 2;

	/**
	 * The number of structural features of the '<em>Editor Part Initializer</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDITOR_PART_INITIALIZER_FEATURE_COUNT = PalettePackage.INITIALIZER_FEATURE_COUNT + 0;


	/**
	 * Returns the meta object for class '{@link org.eclipse.e4.xwt.tools.ui.model.workbench.StaticPartInitializer <em>Static Part Initializer</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Static Part Initializer</em>'.
	 * @see org.eclipse.e4.xwt.tools.ui.model.workbench.StaticPartInitializer
	 * @generated
	 */
	EClass getStaticPartInitializer();

	/**
	 * Returns the meta object for class '{@link org.eclipse.e4.xwt.tools.ui.model.workbench.DynamicPartInitializer <em>Dynamic Part Initializer</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Dynamic Part Initializer</em>'.
	 * @see org.eclipse.e4.xwt.tools.ui.model.workbench.DynamicPartInitializer
	 * @generated
	 */
	EClass getDynamicPartInitializer();

	/**
	 * Returns the meta object for class '{@link org.eclipse.e4.xwt.tools.ui.model.workbench.EditorPartInitializer <em>Editor Part Initializer</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Editor Part Initializer</em>'.
	 * @see org.eclipse.e4.xwt.tools.ui.model.workbench.EditorPartInitializer
	 * @generated
	 */
	EClass getEditorPartInitializer();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	WorkbenchFactory getWorkbenchFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.e4.xwt.tools.ui.model.workbench.impl.StaticPartInitializerImpl <em>Static Part Initializer</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.e4.xwt.tools.ui.model.workbench.impl.StaticPartInitializerImpl
		 * @see org.eclipse.e4.xwt.tools.ui.model.workbench.impl.WorkbenchPackageImpl#getStaticPartInitializer()
		 * @generated
		 */
		EClass STATIC_PART_INITIALIZER = eINSTANCE.getStaticPartInitializer();

		/**
		 * The meta object literal for the '{@link org.eclipse.e4.xwt.tools.ui.model.workbench.impl.DynamicPartInitializerImpl <em>Dynamic Part Initializer</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.e4.xwt.tools.ui.model.workbench.impl.DynamicPartInitializerImpl
		 * @see org.eclipse.e4.xwt.tools.ui.model.workbench.impl.WorkbenchPackageImpl#getDynamicPartInitializer()
		 * @generated
		 */
		EClass DYNAMIC_PART_INITIALIZER = eINSTANCE.getDynamicPartInitializer();

		/**
		 * The meta object literal for the '{@link org.eclipse.e4.xwt.tools.ui.model.workbench.impl.EditorPartInitializerImpl <em>Editor Part Initializer</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.e4.xwt.tools.ui.model.workbench.impl.EditorPartInitializerImpl
		 * @see org.eclipse.e4.xwt.tools.ui.model.workbench.impl.WorkbenchPackageImpl#getEditorPartInitializer()
		 * @generated
		 */
		EClass EDITOR_PART_INITIALIZER = eINSTANCE.getEditorPartInitializer();

	}

} //WorkbenchPackage
