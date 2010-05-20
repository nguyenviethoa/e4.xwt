/*******************************************************************************
 * Copyright (c) 2006, 2010 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.ui.designer.dialogs;

import java.net.URL;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.tools.ui.dataform.AbstractDataForm;
import org.eclipse.e4.tools.ui.dataform.DataForms;
import org.eclipse.e4.xwt.databinding.BindingContext;
import org.eclipse.e4.xwt.emf.EMFBinding;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class ElementInitializeDialog extends TitleAreaDialog {

	private EObject container;
	private EObject eObject;
	private IProject project;

	public ElementInitializeDialog(Shell parentShell, IProject project,
			EObject container, EObject eObject) {
		super(parentShell);
		this.project = project;
		this.container = container;
		this.eObject = eObject;
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Element Initialization Dialog");
	}

	@Override
	protected boolean isResizable() {
		return true;
	}
	
	protected Control createDialogArea(Composite parent) {
		EMFBinding.initialze();
		Composite control = (Composite) super.createDialogArea(parent);

		AbstractDataForm widget = DataForms
				.getWidget(control, eObject.eClass());
		if (widget == null || widget.isDisposed()) {
			setErrorMessage("Can not initialize.");
		} else {
			BindingContext bindingContext = widget.getBindingContext();
			final AggregateValidationStatus validationStatus = bindingContext
					.getStatus();
			validationStatus.addChangeListener(new IChangeListener() {
				public void handleChange(ChangeEvent event) {
					IStatus status = (IStatus) validationStatus.getValue();
					setMessage(status);
				}
			});
			widget.setNewObject(eObject);
			widget.setProject(project);
			widget.setContainer(container);
		}
		setTitle(eObject.eClass().getName() + " Creation");
		setMessage("Element Initialization" );
		return control;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		Button button = getButton(IDialogConstants.OK_ID);
		if (button != null && !button.isDisposed()) {
			button.setEnabled(false);
		}
	}

	/**
	 * In fact, severity of status is different from the type of messages.
	 */
	protected void setMessage(IStatus status) {
		if (status == null || status.isOK()) {
			setMessage((String) null);
		} else {
			int severity = status.getSeverity();
			String message = status.getMessage();
			switch (severity) {
			case IStatus.ERROR:
				setMessage(message, IMessageProvider.ERROR);
				break;
			case IStatus.INFO:
				setMessage(message, IMessageProvider.INFORMATION);
				break;
			case IStatus.WARNING:
				setMessage(message, IMessageProvider.WARNING);
				break;
			default:
				setMessage((String) null);
				break;
			}
		}
		Button button = getButton(IDialogConstants.OK_ID);
		if (button != null && !button.isDisposed()) {
			button.setEnabled(status == null
					|| status.getSeverity() != IStatus.ERROR);
		}
	}

	public URL getContentURL() {
		return DataForms.findWidget(eObject.eClass());
	}
}
