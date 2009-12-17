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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.ui.model.application.MApplicationPackage;
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
	private List<EClass> allClasses;

	public Resource getPaletteResource() {
		if (resource == null) {
			try {
				resource = new ResourceImpl();
				palette = PaletteFactory.eINSTANCE.createPalette();
				palette.setName("E4 Designer Palette");
				resource.getContents().add(palette);
				collectClasses();
				createEntries();
			} catch (Exception e) {
			}
		}
		return resource;
	}

	private void createEntries() {
		EList<Entry> container = palette.getEntries();
		// Container
		Entry entry = createEntry(container, "Container");
		List<EClass> others = new ArrayList<EClass>(allClasses);
		for (EClass eClass : allClasses) {
			EList<Entry> entries = entry.getEntries();
			if (MApplicationPackage.Literals.ELEMENT_CONTAINER
					.isSuperTypeOf(eClass)) {
				createEntry(entries, eClass);
				others.remove(eClass);
			}
		}
		// all
		entry = createEntry(container, "Element");
		for (EClass eClass : allClasses) {
			if (MApplicationPackage.Literals.UI_ELEMENT.isSuperTypeOf(eClass)) {
				createEntry(entry.getEntries(), eClass);
				others.remove(eClass);
			}
		}
		// Window
		entry = createRootEntry(container, MApplicationPackage.Literals.WINDOW);
		// Menu
		entry = createRootEntry(container, MApplicationPackage.Literals.MENU);
		// Perspective
		entry = createRootEntry(container,
				MApplicationPackage.Literals.PERSPECTIVE);
		// ToolBar
		entry = createRootEntry(container,
				MApplicationPackage.Literals.TOOL_BAR);
		createRootEntry(entry.getEntries(),
				MApplicationPackage.Literals.TOOL_ITEM);
		// Part
		entry = createRootEntry(container, MApplicationPackage.Literals.PART);
		// Handler
		entry = createRootEntry(container, MApplicationPackage.Literals.HANDLER);
		others.remove(MApplicationPackage.Literals.HANDLER);
		// Command
		entry = createRootEntry(container, MApplicationPackage.Literals.COMMAND);
		others.remove(MApplicationPackage.Literals.COMMAND);
		// Others
		if (!others.isEmpty()) {
			entry = createEntry(container, "Other");
			for (EClass eClass : others) {
				createEntry(entry.getEntries(), eClass);
			}
		}
	}

	private void collectClasses() throws IllegalArgumentException,
			IllegalAccessException {
		if (allClasses != null) {
			allClasses.clear();
		} else {
			allClasses = new ArrayList<EClass>();
		}
		Field[] fields = MApplicationPackage.Literals.class.getFields();
		for (int i = 0; i < fields.length; i++) {
			Object value = fields[i].get(null);
			if (value instanceof EClass) {
				EClass eClass = (EClass) value;
				if (!eClass.isAbstract() && !eClass.isInterface()) {
					allClasses.add(eClass);
				}
			}
		}
	}

	private Entry createRootEntry(EList<Entry> container, EClass root) {
		Entry entry = createEntry(palette.getEntries(), root);
		String rootName = root.getName().toLowerCase();
		for (EClass eClass : allClasses) {
			String lowerCase = eClass.getName().toLowerCase();
			if (lowerCase.startsWith(rootName) || root.isSuperTypeOf(eClass)) {
				createEntry(entry.getEntries(), eClass);
			}
		}
		container.add(entry);
		return entry;
	}

	private Entry createEntry(EList<Entry> container, EClass eClass) {
		String pkgName = eClass.getEPackage().getName();
		String className = eClass.getName();
		Entry entry = createEntry(container, className);
		entry.setId(pkgName + "." + className);
		return entry;
	}

	private Entry createEntry(EList<Entry> container, String name) {
		Entry entry = PaletteFactory.eINSTANCE.createEntry();
		entry.setName(name);
		container.add(entry);
		return entry;
	}
}
