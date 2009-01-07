/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.vex.palette.actions;

import org.eclipse.e4.xwt.vex.Activator;
import org.eclipse.e4.xwt.vex.VEXEditor;
import org.eclipse.e4.xwt.vex.palette.CustomPalettePage;
import org.eclipse.e4.xwt.vex.palette.part.DynamicPaletteViewer;
import org.eclipse.e4.xwt.vex.swt.CustomSashForm;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;

public class HideDynamicPartPaletteAction extends Action {
	
	private PaletteViewer paletteViewer;
	private DynamicPaletteViewer dynamicPaletteViewer;
	private CustomSashForm dynamicAndCustomizeSashForm;
	
	public HideDynamicPartPaletteAction() {
		super();
		// TODO Auto-generated constructor stub
		setText("Hide/Show Contextual Tools");
		setImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/full/obj16/hide2.gif"));
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Object editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (editor instanceof VEXEditor) {
			paletteViewer = ((CustomPalettePage) ((VEXEditor) editor).getVEXEditorPalettePage()).getPaletteViewer();
		}
		
		Object objectDynamicPalette = paletteViewer.getProperty("Dynamic_PaletteViewer");
		if (objectDynamicPalette instanceof DynamicPaletteViewer) {
			dynamicPaletteViewer = (DynamicPaletteViewer) objectDynamicPalette;
		}
		
		Object dynamicSashForm = dynamicPaletteViewer.getProperty("DynamicAndCustomizeSashForm");
		if (dynamicSashForm instanceof CustomSashForm) {
			dynamicAndCustomizeSashForm = (CustomSashForm) dynamicSashForm;
		}
		
		if(isChecked()){
			setChecked(false);
			dynamicPaletteViewer.getControl().setVisible(true);
			dynamicAndCustomizeSashForm.setWeights((new int[] { 1, 1 }));
		}else {
			setChecked(true);
			dynamicPaletteViewer.getControl().setVisible(false);
			
			dynamicAndCustomizeSashForm.setWeights((new int[] { 0, 1 }));
		}
	}

	
}
