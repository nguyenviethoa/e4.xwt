package org.eclipse.e4.xwt.vex.palette.customize.actions;

import org.eclipse.e4.xwt.vex.palette.customize.CustomizeComponentFactory;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;

public class DeleteCustomizeComponentAction extends Action {
	private String selectComponentName = "";
	private PaletteViewer paletteViewer;

	public DeleteCustomizeComponentAction(PaletteViewer paletteViewer, String selectComponentName) {
		super();
		setText("&Delete Selected Component");
		this.paletteViewer = paletteViewer;
		this.selectComponentName = selectComponentName;
	}

	@Override
	public void run() {
		super.run();
		if (MessageDialog.openConfirm(paletteViewer.getControl().getShell(), "Confirm", "Are you sure to delete customize component " + selectComponentName + " ?")) {
			// delete customize component
			CustomizeComponentFactory customizeComponentFactory = CustomizeComponentFactory.getCustomizeComponentFactory();
			customizeComponentFactory.deleteComponent(selectComponentName);
		}
	}

}
