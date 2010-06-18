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
package org.eclipse.e4.tools.ui.designer.palette;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.tools.ui.designer.E4DesignerPlugin;
import org.eclipse.e4.tools.ui.designer.utils.ApplicationModelHelper;
import org.eclipse.e4.ui.model.application.commands.impl.CommandsPackageImpl;
import org.eclipse.e4.ui.model.application.ui.advanced.impl.AdvancedPackageImpl;
import org.eclipse.e4.ui.model.application.ui.basic.impl.BasicPackageImpl;
import org.eclipse.e4.ui.model.application.ui.impl.UiPackageImpl;
import org.eclipse.e4.ui.model.application.ui.menu.impl.MenuPackageImpl;
import org.eclipse.e4.xwt.tools.ui.palette.Entry;
import org.eclipse.e4.xwt.tools.ui.palette.Palette;
import org.eclipse.e4.xwt.tools.ui.palette.PaletteFactory;
import org.eclipse.e4.xwt.tools.ui.palette.page.resources.EntryResourceProvider;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;

/**
 * @author jin.liu(jin.liu@soyatec.com)
 */
public class E4PaletteProvider extends EntryResourceProvider {
	private Resource resource;
	private Palette palette;

	// private List<EClass> allClasses;

	public Resource getPaletteResource() {
		if (resource == null) {
			try {
				resource = new ResourceImpl();
				palette = PaletteFactory.eINSTANCE.createPalette();
				palette.setName("e4 Visual Designer Palette");
				resource.getContents().add(palette);
				// collectClasses();
				createEntries();
			} catch (Exception e) {
				E4DesignerPlugin.logWarning(e);
			}
		}
		return resource;
	}

	private void exclusiveDone(Entry entry, List<EClass> others) {
		others.remove(entry.getType());

		for (Entry child : entry.getEntries()) {
			exclusiveDone(child, others);
		}
	}

	private void createEntries() {
		EList<Entry> container = palette.getEntries();
		// Container
		Entry entry = createEntry(container, "Container");
		List<EClass> applicationClasses = ApplicationModelHelper
				.getApplicationClasses();
		List<EClass> others = new ArrayList<EClass>(applicationClasses);
		for (EClass eClass : applicationClasses) {
			if (eClass.isAbstract() || eClass.isInterface()) {
				continue;
			}
			EList<Entry> entries = entry.getEntries();
			if (UiPackageImpl.Literals.ELEMENT_CONTAINER.isSuperTypeOf(eClass)
					&& !MenuPackageImpl.Literals.MENU_ITEM
							.isSuperTypeOf(eClass)
					&& !MenuPackageImpl.Literals.MENU.isSuperTypeOf(eClass)
					&& !MenuPackageImpl.Literals.TOOL_ITEM
							.isSuperTypeOf(eClass)) {
				createEntry(entries, eClass);
			}
		}
		// UI Element
		entry = createEntry(container, "Element");
		for (EClass eClass : applicationClasses) {
			if (eClass.isAbstract() || eClass.isInterface()) {
				continue;
			}
			if (UiPackageImpl.Literals.UI_ELEMENT.isSuperTypeOf(eClass)
					&& !UiPackageImpl.Literals.ELEMENT_CONTAINER
							.isSuperTypeOf(eClass)
					&& !MenuPackageImpl.Literals.MENU_ITEM
							.isSuperTypeOf(eClass)
					&& !MenuPackageImpl.Literals.MENU.isSuperTypeOf(eClass)
					&& !MenuPackageImpl.Literals.TOOL_ITEM
							.isSuperTypeOf(eClass)) {
				createEntry(entry.getEntries(), eClass);
			}
		}
		// Window
		entry = createRootEntry(container, BasicPackageImpl.Literals.WINDOW);
		// Menu
		entry = createRootEntry(container, MenuPackageImpl.Literals.MENU);
		createRootEntry(entry.getEntries(), MenuPackageImpl.Literals.MENU_ITEM);

		// Perspective
		entry = createRootEntry(container,
				AdvancedPackageImpl.Literals.PERSPECTIVE);
		// ToolBar
		entry = createRootEntry(container, MenuPackageImpl.Literals.TOOL_BAR);
		createRootEntry(entry.getEntries(), MenuPackageImpl.Literals.TOOL_ITEM);
		// Part
		entry = createRootEntry(container, BasicPackageImpl.Literals.PART);
		// Handler
		entry = createRootEntry(container, CommandsPackageImpl.Literals.HANDLER);
		others.remove(CommandsPackageImpl.Literals.HANDLER);
		// Command
		entry = createRootEntry(container, CommandsPackageImpl.Literals.COMMAND);
		others.remove(CommandsPackageImpl.Literals.COMMAND);

		// Others
		for (Entry childEntry : container) {
			exclusiveDone(childEntry, others);
		}

		if (!others.isEmpty()) {
			entry = createEntry(container, "Other");
			for (EClass eClass : others) {
				if (eClass.isAbstract() || eClass.isInterface()) {
					continue;
				}
				createEntry(entry.getEntries(), eClass);
			}
		}
	}

	// private void collectClasses() throws IllegalArgumentException,
	// IllegalAccessException {
	// if (allClasses != null) {
	// allClasses.clear();
	// } else {
	// allClasses = new ArrayList<EClass>();
	// }
	//
	// Class<?>[] packageClasses = new Class[] {
	// ApplicationPackageImpl.Literals.class,
	// BasicPackageImpl.Literals.class, UiPackageImpl.Literals.class,
	// CommandsPackageImpl.Literals.class,
	// MenuPackageImpl.Literals.class,
	// AdvancedPackageImpl.Literals.class };
	//
	// for (Class<?> packageClass : packageClasses) {
	// Field[] fields = packageClass.getFields();
	// EClass applicationElementClass = ApplicationPackageImpl.eINSTANCE
	// .getApplicationElement();
	//
	// for (int i = 0; i < fields.length; i++) {
	// Object value = fields[i].get(null);
	// if (value instanceof EClass) {
	// EClass eClass = (EClass) value;
	// if (applicationElementClass.isSuperTypeOf(eClass)) {
	// allClasses.add(eClass);
	// }
	// }
	// }
	// }
	// }

	private Entry createRootEntry(EList<Entry> container, EClass root) {
		Entry entry = createEntry(palette.getEntries(), root);
		String rootName = root.getName().toLowerCase();
		for (EClass eClass : ApplicationModelHelper.getApplicationClasses()) {
			if (eClass.isAbstract() || eClass.isInterface()) {
				continue;
			}
			String lowerCase = eClass.getName().toLowerCase();
			if (lowerCase.startsWith(rootName) || root.isSuperTypeOf(eClass)) {
				createEntry(entry.getEntries(), eClass);
			}
		}
		container.add(entry);
		return entry;
	}

	private Entry createEntry(EList<Entry> container, EClass eClass) {
		Entry entry = createEntry(eClass);
		container.add(entry);
		return entry;
	}

	private Entry createEntry(EList<Entry> container, String name) {
		Entry entry = createEntry(name);
		container.add(entry);
		return entry;
	}

	public static Entry createEntry(String name) {
		Entry entry = PaletteFactory.eINSTANCE.createEntry();
		entry.setName(name);
		entry.setIcon("platform:/plugin/org.eclipse.e4.ui.model.workbench.edit/icons/full/obj16/"
				+ name + ".gif");
		return entry;
	}

	public static Entry createEntry(EClass eClass) {
		String pkgName = eClass.getEPackage().getName();
		String className = eClass.getName();
		Entry entry = createEntry(className);
		entry.setId(pkgName + "." + className);
		entry.setType(eClass);
		entry.setIcon("platform:/plugin/org.eclipse.e4.ui.model.workbench.edit/icons/full/obj16/"
				+ className + ".gif");
		return entry;
	}
}
