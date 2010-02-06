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

import org.eclipse.e4.xwt.tools.ui.model.workbench.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class WorkbenchFactoryImpl extends EFactoryImpl implements WorkbenchFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static WorkbenchFactory init() {
		try {
			WorkbenchFactory theWorkbenchFactory = (WorkbenchFactory)EPackage.Registry.INSTANCE.getEFactory("http://www.eclipse.org/e4/xwt/workbench/desiger"); 
			if (theWorkbenchFactory != null) {
				return theWorkbenchFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new WorkbenchFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public WorkbenchFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case WorkbenchPackage.STATIC_PART_INITIALIZER: return createStaticPartInitializer();
			case WorkbenchPackage.DYNAMIC_PART_INITIALIZER: return createDynamicPartInitializer();
			case WorkbenchPackage.EDITOR_PART_INITIALIZER: return createEditorPartInitializer();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public StaticPartInitializer createStaticPartInitializer() {
		StaticPartInitializerImpl staticPartInitializer = new StaticPartInitializerImpl();
		return staticPartInitializer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DynamicPartInitializer createDynamicPartInitializer() {
		DynamicPartInitializerImpl dynamicPartInitializer = new DynamicPartInitializerImpl();
		return dynamicPartInitializer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EditorPartInitializer createEditorPartInitializer() {
		EditorPartInitializerImpl editorPartInitializer = new EditorPartInitializerImpl();
		return editorPartInitializer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public WorkbenchPackage getWorkbenchPackage() {
		return (WorkbenchPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static WorkbenchPackage getPackage() {
		return WorkbenchPackage.eINSTANCE;
	}

} //WorkbenchFactoryImpl
