package org.eclipse.e4.xwt.tests.controls.button.command;

import org.eclipse.e4.xwt.input.RoutedCommand;
import org.eclipse.jface.dialogs.MessageDialog;

public class MessageCommand extends RoutedCommand {

	public MessageCommand() {
	}

	@Override
	public void execute(Object parameter) {
		MessageDialog.openInformation(null, "Message", "Command message");
	}
}
