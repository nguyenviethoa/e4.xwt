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

import org.eclipse.e4.xwt.tools.ui.model.workbench.DynamicPartInitializer;
import org.eclipse.e4.xwt.tools.ui.model.workbench.EditorPartInitializer;
import org.eclipse.e4.xwt.tools.ui.model.workbench.StaticPartInitializer;
import org.eclipse.e4.xwt.tools.ui.model.workbench.WorkbenchFactory;
import org.eclipse.e4.xwt.tools.ui.model.workbench.WorkbenchPackage;

import org.eclipse.e4.xwt.tools.ui.palette.PalettePackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class WorkbenchPackageImpl extends EPackageImpl implements WorkbenchPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass staticPartInitializerEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dynamicPartInitializerEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass editorPartInitializerEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.e4.xwt.tools.ui.model.workbench.WorkbenchPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private WorkbenchPackageImpl() {
		super(eNS_URI, WorkbenchFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 * 
	 * <p>This method is used to initialize {@link WorkbenchPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static WorkbenchPackage init() {
		if (isInited) return (WorkbenchPackage)EPackage.Registry.INSTANCE.getEPackage(WorkbenchPackage.eNS_URI);

		// Obtain or create and register package
		WorkbenchPackageImpl theWorkbenchPackage = (WorkbenchPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof WorkbenchPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new WorkbenchPackageImpl());

		isInited = true;

		// Initialize simple dependencies
		PalettePackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theWorkbenchPackage.createPackageContents();

		// Initialize created meta-data
		theWorkbenchPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theWorkbenchPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(WorkbenchPackage.eNS_URI, theWorkbenchPackage);
		return theWorkbenchPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getStaticPartInitializer() {
		return staticPartInitializerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDynamicPartInitializer() {
		return dynamicPartInitializerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEditorPartInitializer() {
		return editorPartInitializerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public WorkbenchFactory getWorkbenchFactory() {
		return (WorkbenchFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		staticPartInitializerEClass = createEClass(STATIC_PART_INITIALIZER);

		dynamicPartInitializerEClass = createEClass(DYNAMIC_PART_INITIALIZER);

		editorPartInitializerEClass = createEClass(EDITOR_PART_INITIALIZER);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		PalettePackage thePalettePackage = (PalettePackage)EPackage.Registry.INSTANCE.getEPackage(PalettePackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		staticPartInitializerEClass.getESuperTypes().add(thePalettePackage.getInitializer());
		dynamicPartInitializerEClass.getESuperTypes().add(thePalettePackage.getInitializer());
		editorPartInitializerEClass.getESuperTypes().add(thePalettePackage.getInitializer());

		// Initialize classes and features; add operations and parameters
		initEClass(staticPartInitializerEClass, StaticPartInitializer.class, "StaticPartInitializer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(dynamicPartInitializerEClass, DynamicPartInitializer.class, "DynamicPartInitializer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(editorPartInitializerEClass, EditorPartInitializer.class, "EditorPartInitializer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);
	}

} //WorkbenchPackageImpl
