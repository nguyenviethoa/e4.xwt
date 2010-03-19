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
package org.eclipse.e4.tools.ui.designer.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage.ImportsManager;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class EMFCodegen {

	public static String genDynamicModel(ImportsManager imports,
			EPackage ePackage, EObject initializeObj, boolean appendReturn,
			IProgressMonitor monitor) {
		if (ePackage == null) {
			return null;
		}
		EClass initializeType = initializeObj.eClass();

		StringBuffer buf = new StringBuffer();
		String lineDelim = "\n";
		String packageName = ePackage.getName() + "Package";
		appendLine(buf, "EPackage " + packageName
				+ " = EcoreFactory.eINSTANCE.createEPackage();", lineDelim);
		appendLine(buf, packageName + ".setName(\"" + packageName + "\");",
				lineDelim);
		appendLine(buf, "", lineDelim);

		if (imports != null) {
			imports.addImport(EPackage.class.getName());
			imports.addImport(EcoreFactory.class.getName());
			imports.addImport(EClass.class.getName());
			imports.addImport(EAttribute.class.getName());
			imports.addImport(EStructuralFeature.class.getName());
			imports.addImport(EcorePackage.class.getName());
			imports.addImport(EObject.class.getName());
			imports.addImport(EcoreUtil.class.getName());
		}

		// Create dynamic classes.
		for (EClassifier eClassifier : ePackage.getEClassifiers()) {
			if (!(eClassifier instanceof EClass)) {
				System.out.println("FIXME");
				continue;
			}
			EClass eClass = (EClass) eClassifier;
			String name = eClass.getName();
			String className = normalClassName(name) + "Class";
			appendLine(buf, "EClass " + className
					+ " = EcoreFactory.eINSTANCE.createEClass();", lineDelim);
			appendLine(buf, className + ".setName(\"" + name + "\");",
					lineDelim);
			appendLine(buf, packageName + ".getEClassifiers().add(" + className
					+ ");", lineDelim);
			appendLine(buf, "", lineDelim);
		}

		List<String> existingNames = new ArrayList<String>();
		int i = 1;
		for (EClassifier eClassifier : ePackage.getEClassifiers()) {
			if (!(eClassifier instanceof EClass)) {
				continue;
			}
			EClass eClass = (EClass) eClassifier;
			String name = eClass.getName();
			String className = normalClassName(name) + "Class";
			EList<EStructuralFeature> attributes = eClass
					.getEStructuralFeatures();
			for (EStructuralFeature attr : attributes) {
				String attrName = attr.getName();
				while (existingNames.contains(attrName)) {
					attrName = attrName + (i++);
				}
				existingNames.add(attrName);
				appendLine(buf, "EAttribute " + attrName
						+ " = EcoreFactory.eINSTANCE.createEAttribute();",
						lineDelim);
				appendLine(buf, attrName + ".setName(\"" + attr.getName()
						+ "\");", lineDelim);
				appendLine(buf, attrName + ".setEType("
						+ getETypeStr(ePackage, attr.getEType()) + ");",
						lineDelim);

				appendLine(buf, className + ".getEStructuralFeatures().add("
						+ attrName + ");", lineDelim);
				appendLine(buf, "", lineDelim);
			}
			appendLine(buf, "", lineDelim);
		}
		String targetClassName = normalClassName(initializeType.getName())
				+ "Class";

		appendLine(buf, "return " + targetClassName + ";", lineDelim);
		return buf.toString();
	}

	public static String genDynamicContents(ImportsManager imports,
			EPackage ePackage, EObject initializeObj, boolean appendReturn,
			IProgressMonitor monitor) {
		StringBuffer buf = new StringBuffer();
		String lineDelim = "\n";

		EClass initializeType = initializeObj.eClass();

		// Initialize objects.
		genDynamicInitializeContents(null, initializeObj, initializeType, buf,
				lineDelim);

		appendLine(buf, "", lineDelim);
		if (appendReturn && initializeType != null) {
			appendLine(buf, "return "
					+ normalClassName(initializeType.getName()) + "Object;",
					lineDelim);
		}
		return buf.toString();
	}

	private static void genDynamicInitializeContents(String parentType, EObject initializeObj,
			EClass initializeType, StringBuffer buf, String lineDelim) {
		if (initializeType != null) {
			String name = normalClassName(initializeType.getName());
			String objectNameType = name + "ObjectType";
			String objectName = name + "Object";
			if (parentType == null) {
				appendLine(buf, "EClass " + objectNameType + " = getDataContextType();", lineDelim);
				appendLine(buf, "EObject " + objectName + " = EcoreUtil.create(" + objectNameType + ");", lineDelim);
			}
			else {
				objectNameType = name + "Class";
				// String parentType = normalClassName(initializeObj.eClass().getName()) + "Object";
				appendLine(buf, "EClass " + objectNameType + " = (EClass)" + parentType + ".eClass().getEPackage().getEClassifier(\"" + initializeType.getName() + "\");", lineDelim);				
				appendLine(buf, "EObject " + objectName + " = EcoreUtil.create("
					+ objectNameType + ");", lineDelim);
			}
			EList<EStructuralFeature> features = initializeType
					.getEStructuralFeatures();
			for (EStructuralFeature sf : features) {
				String attrName = sf.getName();
				String attrVarName = attrName + "Attribute";
				if (initializeObj != null && initializeObj.eIsSet(sf)) {
					Object value = initializeObj.eGet(sf);
					if (value instanceof EObject) {
						EObject eObj = (EObject) value;
						EClass valueType = eObj.eClass();
						String valueName = normalClassName(valueType.getName())
								+ "Object";
						genDynamicInitializeContents(objectName, eObj, valueType, buf,
								lineDelim);
						appendLine(buf, "EStructuralFeature " + attrVarName + " = "
								+ objectNameType + ".getEStructuralFeature(\"" + attrName + "\");", lineDelim);
						appendLine(buf, objectName + ".eSet(" + attrVarName + ", "
								+ valueName + ");", lineDelim);
					} else {
						String appendValue = value.toString();
						if (value instanceof String) {
							appendValue = "\"" + value + "\"";
						}
						appendLine(buf, "EStructuralFeature " + attrVarName + " = "
								+ objectNameType + ".getEStructuralFeature(\"" + attrName + "\");", lineDelim);
						appendLine(buf, objectName + ".eSet(" + attrVarName + ", "
								+ appendValue + ");", lineDelim);
					}
				} else if (sf instanceof EReference) {
					EReference reference = (EReference) sf;
					EClassifier classifier = reference.getEType();
					if (classifier instanceof EClass) {
						EClass type = (EClass) classifier;
						if (!type.isAbstract()) {
							String valueName = normalClassName(type
									.getName())
									+ "Object";
							genDynamicInitializeContents(objectName, null, type, buf,
									lineDelim);
							appendLine(buf, "EStructuralFeature " + attrVarName + " = "
									+ objectNameType + ".getEStructuralFeature(\"" + attrName + "\");", lineDelim);
							appendLine(buf, objectName + ".eSet(" + attrVarName
									+ ", " + valueName + ");", lineDelim);
						}
					}
				}
			}
		}
	}

	private static String normalClassName(String className) {
		return className.length() > 1 ? Character.toLowerCase(className
				.charAt(0))
				+ className.substring(1) : className.toLowerCase();
	}

	public static String getETypeStr(EPackage ePackage, EClassifier type) {
		if (ePackage.getEClassifiers().contains(type)) {
			String name = ((EClassifier) type).getName();
			return normalClassName(name) + "Class";
		} else if (type instanceof EDataType) {
			String name = ((EDataType) type).getName();
			return "EcorePackage.eINSTANCE.get" + name + "()";
		} else if (type instanceof EClass) {
			return "EcoreUtil.create((EClass) type)";
		}
		return null;
	}

	protected static void appendLine(StringBuffer buf, String content,
			String lineDelim) {
		buf.append(content);
		buf.append(lineDelim);
	}

	public static void main(String[] args) {
		EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
		ePackage.setName("test");

		EClass bookClass = EcoreFactory.eINSTANCE.createEClass();
		ePackage.getEClassifiers().add(bookClass);
		bookClass.setName("Book");

		EAttribute authorFeature = EcoreFactory.eINSTANCE.createEAttribute();
		authorFeature.setName("author");
		authorFeature.setEType(EcorePackage.eINSTANCE.getEString());
		bookClass.getEStructuralFeatures().add(authorFeature);

		EAttribute priceFeature = EcoreFactory.eINSTANCE.createEAttribute();
		priceFeature.setName("price");
		priceFeature.setEType(EcorePackage.eINSTANCE.getEDouble());
		bookClass.getEStructuralFeatures().add(priceFeature);

		EAttribute yearFeature = EcoreFactory.eINSTANCE.createEAttribute();
		yearFeature.setName("year");
		yearFeature.setEType(EcorePackage.eINSTANCE.getEInt());
		bookClass.getEStructuralFeatures().add(yearFeature);

		EClass titleClass = EcoreFactory.eINSTANCE.createEClass();
		ePackage.getEClassifiers().add(titleClass);
		titleClass.setName("Title");

		EAttribute lanFeature = EcoreFactory.eINSTANCE.createEAttribute();
		lanFeature.setName("lan");
		lanFeature.setEType(EcorePackage.eINSTANCE.getEString());
		titleClass.getEStructuralFeatures().add(lanFeature);

		EAttribute textFeature = EcoreFactory.eINSTANCE.createEAttribute();
		textFeature.setName("text");
		textFeature.setEType(EcorePackage.eINSTANCE.getEString());
		titleClass.getEStructuralFeatures().add(textFeature);

		EReference titleFeature = EcoreFactory.eINSTANCE.createEReference();
		titleFeature.setName("title");
		titleFeature.setEType(titleClass);
		bookClass.getEStructuralFeatures().add(titleFeature);

		EObject harryPotter = EcoreUtil.create(bookClass);

		EObject title = EcoreUtil.create(titleClass);

		title.eSet(lanFeature, "en");
		title.eSet(textFeature, "Harry Potter");
		harryPotter.eSet(titleFeature, title);
		harryPotter.eSet(authorFeature, "Neal Stephenson");
		harryPotter.eSet(priceFeature, 29.99);
		harryPotter.eSet(yearFeature, 2005);

		String contents = genDynamicContents(null, ePackage, harryPotter, true,
				null);
		System.out.println(contents);

	}
}
