package org.eclipse.e4.xwt.pde.ui;

import org.eclipse.e4.xwt.pde.PDEPlugin;
import org.eclipse.jface.dialogs.MessageDialog;

public class ExceptionHandle {

	static public void handle(Exception e, String message) {
		if (e.getMessage() != null) {
			message += "\n" + e.getMessage();
		}
		MessageDialog.openError(PDEPlugin.getShell(), "Erreur: ", message);
	}
}
