package org.eclipse.e4.xwt.vex.palette.customize.actions;

import org.eclipse.e4.xwt.vex.palette.customize.CustomizeComponentFactory;
import org.eclipse.e4.xwt.vex.palette.customize.InvokeType;
import org.eclipse.e4.xwt.vex.palette.customize.dialogs.CustomizePaletteDialog;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;

public class ModifyCustomizeComponentAction extends Action {
	private String selectComponentName = "";
	private PaletteViewer paletteViewer;

	public ModifyCustomizeComponentAction(PaletteViewer paletteViewer, String selectComponentName) {
		super();
		setText("&Modify Selected Component");
		this.paletteViewer = paletteViewer;
		this.selectComponentName = selectComponentName;
	}

	@Override
	public void run() {
		super.run();
		// modify customize component
		// CustomizeComponentFactory customizeComponentFactory = CustomizeComponentFactory
		// .getCustomizeComponentFactory();
		Dialog customizeDialog = new CustomizePaletteDialog(InvokeType.Modify, selectComponentName, null);
		customizeDialog.open();
	}

}
