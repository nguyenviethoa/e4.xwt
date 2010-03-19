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

import org.eclipse.e4.ui.model.application.MApplicationPackage;
import org.eclipse.e4.xwt.tools.ui.palette.Entry;
import org.eclipse.e4.xwt.tools.ui.palette.Palette;
import org.eclipse.e4.xwt.tools.ui.palette.PaletteFactory;
import org.eclipse.e4.xwt.tools.ui.palette.page.resources.EntryResourceProvider;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class E4PartContribution extends EntryResourceProvider {

	private Resource resource;
	public Resource getPaletteResource() {
		if (resource == null) {
			resource = new ResourceImpl();
			resource.getContents().add(createPalette());
		}
		return resource;
	}
	private Palette createPalette() {
		Palette palette = PaletteFactory.eINSTANCE.createPalette();
		palette.setName("E4 Part Contribution");

		Entry group = PaletteFactory.eINSTANCE.createEntry();
		group.setName("Part Contributions");

		Entry selectionEntry = E4PaletteProvider
				.createEntry(MApplicationPackage.Literals.PART);
		selectionEntry.setId("e4.contribution.selection");
		selectionEntry.setName("Contributed Selection Part");
		selectionEntry.setInitializer(new E4SelectionPartInitializer());

		Entry inputEntry = E4PaletteProvider
				.createEntry(MApplicationPackage.Literals.INPUT_PART);
		inputEntry.setId("e4.contribution.input");
		inputEntry.setName("Contributed Input Part");
		inputEntry.setInitializer(new E4InputPartInitializer());

		group.getEntries().add(selectionEntry);
		group.getEntries().add(inputEntry);

		palette.getEntries().add(group);
		return palette;
	}

}
