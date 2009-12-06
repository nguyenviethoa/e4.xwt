/*******************************************************************************
 * Copyright (c) 2009 Siemens AG and others.
 * 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 * 
 * Contributors:
 *     Kai Tödter - initial implementation
 *     Yves YANG - ports to XWT
 ******************************************************************************/

package org.eclipse.e4.demo.contacts.views;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.services.annotations.Optional;
import org.eclipse.e4.demo.contacts.handlers.FadeAnimation;
import org.eclipse.e4.demo.contacts.handlers.ThemeUtil;
import org.eclipse.e4.demo.contacts.model.Contact;
import org.eclipse.e4.ui.model.application.MDirtyable;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.xwt.ui.workbench.editors.XWTSaveablePart;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;

public class DetailsView extends XWTSaveablePart {
	@Inject
	public DetailsView(Composite parent, MDirtyable dirtyable) {
		super(parent, dirtyable);
	}

	/**
	 * switch the content change with fade animation. 
	 * 
	 */
	protected void refresh(URL url, Object dataContext, ClassLoader loader) {
		FadeAnimation animation = new FadeAnimation(getParent()); 
		animation.setStep(10);
		super.refresh(url, dataContext, loader);
		animation.play();
	}
	
	public void doSave(@Optional IProgressMonitor monitor) throws IOException,
			InterruptedException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		monitor.beginTask("Saving contact details to vCard...", 16);
		Contact modifiedContact = (Contact) getDataContext();
		saveAsVCard(modifiedContact, modifiedContact.getSourceFile());
		updatePartTitle(modifiedContact);
		monitor.done();
	}

	private String getName(Contact contact, String charSet) {
		StringBuilder builder = new StringBuilder();
		builder.append("N;").append(charSet).append(':'); //$NON-NLS-1$
		builder.append(contact.getLastName()).append(';');
		builder.append(contact.getFirstName()).append(';');
		builder.append(contact.getMiddleName());

		String title = contact.getTitle();
		if (title.length() != 0) {
			builder.append(';').append(title);
		}

		builder.append('\n');
		return builder.toString();
	}

	private void saveAsVCard(Contact contact, String fileName)
			throws IOException {
		String charSet = "CHARSET=" + Charset.defaultCharset().name();
		String vCard = "BEGIN:VCARD" + "\nVERSION:2.1" + "\n"
				+ getName(contact, charSet) + "FN;" + charSet + ":"
				+ contact.getFirstName() + " " + contact.getLastName()
				+ "\nORG;" + charSet + ":" + contact.getCompany() + "\nTITLE:"
				+ contact.getJobTitle() + "\nNOTE:" + contact.getNote()
				+ "\nTEL;WORK;VOICE:" + contact.getPhone()
				+ "\nTEL;CELL;VOICE:" + contact.getMobile() + "\nADR;WORK;"
				+ charSet + ":" + ";;" + contact.getStreet() + ";"
				+ contact.getCity() + ";" + contact.getState() + ";"
				+ contact.getZip() + ";" + contact.getCountry() + "\nURL;WORK:"
				+ contact.getWebPage() + "\nEMAIL;PREF;INTERNET:"
				+ contact.getEmail() + "\n";

		if (!contact.getJpegString().equals("")) {
			vCard += "PHOTO;TYPE=JPEG;ENCODING=BASE64:\n "
					+ contact.getJpegString() + "\n";
		}

		vCard += "END:VCARD\n";

		PrintWriter out = new PrintWriter(fileName, "Cp1252");
		out.println(vCard);
		out.close();
	}

	private void updatePartTitle(Contact contact) {
		StringBuffer title = new StringBuffer("Details of ");
		title.append(contact.getFirstName()).append(' ').append(
				contact.getLastName());
		super.updatePartTitle(title.toString());
	}

	@Inject
	public void setSelection(@Optional @Named(IServiceConstants.SELECTION) Contact contact) {
		if (contact != null) {
			if (isDirty()) {
				MessageDialog dialog = new MessageDialog(getShell(), "Save vCard", null,
						"The current vCard has been modified. Save changes?",
						MessageDialog.CONFIRM, new String[] {
								IDialogConstants.YES_LABEL,
								IDialogConstants.NO_LABEL }, 0);
				dialog.create();
				ThemeUtil.applyDialogStyles(getStyleEngine(), dialog.getShell());
				if (dialog.open() == Window.OK) {
					ParameterizedCommand saveCommand = getCommandService()
							.createCommand("contacts.save",
									Collections.EMPTY_MAP);
					getHandlerService().executeHandler(saveCommand);
				}
			}

			updatePartTitle(contact);
		} else {
			updatePartTitle("Details");
		}
		setDataContext(contact);
	}
}
