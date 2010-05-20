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
package org.eclipse.e4.tools.ui.designer.properties;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.tools.ui.dataform.AbstractDataForm;
import org.eclipse.e4.tools.ui.dataform.DataForms;
import org.eclipse.e4.ui.model.application.impl.ApplicationPackageImpl;
import org.eclipse.e4.xwt.databinding.BindingContext;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class AppearanceSection extends AbstractPropertySection {

	private EObject eObj;
	private Map<EClass, Control> controlMap = new HashMap<EClass, Control>(1);
	private Composite stackComp;
	private Label emptyLabel;
	private StackLayout stackLayout;

	private Text messageLabel;
	private Label imageLabel;
	private GridData imageLabelData;
	private GridData messageLableData;

	private AggregateValidationStatus validation;
	private ValidationStatusListener listener = new ValidationStatusListener();

	public void createControls(Composite parent,
			TabbedPropertySheetPage propertyPage) {
		super.createControls(parent, propertyPage);
		Composite control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout(2, false));
		imageLabel = new Label(control, SWT.NONE);
		imageLabel.setVisible(false);
		imageLabelData = new GridData();
		imageLabel.setLayoutData(imageLabelData);
		imageLabelData.exclude = true;
		messageLabel = new Text(control, SWT.WRAP | SWT.READ_ONLY);
		messageLableData = new GridData(GridData.FILL_HORIZONTAL
				| GridData.GRAB_HORIZONTAL);
		messageLableData.exclude = true;
		messageLabel.setLayoutData(messageLableData);
		messageLabel.setVisible(false);

		stackComp = new Composite(control, SWT.NONE);
		stackComp.setLayoutData(GridDataFactory.fillDefaults().grab(true, true)
				.span(2, 1).create());
		stackLayout = new StackLayout();
		stackComp.setLayout(stackLayout);
		emptyLabel = new Label(stackComp, SWT.NONE);
		emptyLabel.setText("Properties page is not avariable.");
		controlMap.put(null, emptyLabel);
	}

	public boolean shouldUseExtraSpace() {
		return true;
	}

	protected void setMessage(IStatus status) {
		if (imageLabel == null || imageLabel.isDisposed()
				|| messageLabel == null || messageLabel.isDisposed()) {
			return;
		}
		Image image = null;
		String message = null;
		if (status != null && !status.isOK()) {
			int severity = status.getSeverity();
			switch (severity) {
			case IStatus.ERROR:
				image = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_ERROR);
				break;
			case IStatus.WARNING:
				image = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_WARNING);
				break;
			case IStatus.INFO:
				image = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_INFO);
				break;
			}
			message = status.getMessage();
		}
		boolean visible = image != null;
		imageLabel.setImage(image);
		imageLabelData.exclude = messageLableData.exclude = !visible;
		imageLabel.setVisible(visible);
		messageLabel.setVisible(visible);
		messageLabel.setText(message == null ? "" : message);
		imageLabel.getParent().layout(
				new Control[] { imageLabel, messageLabel });
	}

	public void refresh() {
		if (stackComp == null || stackComp.isDisposed()) {
			return;
		}
		EClass newType = null;
		if (eObj != null) {
			newType = eObj.eClass();
		}
		if (validation != null) {
			validation.removeChangeListener(listener);
		}
		Control control = controlMap.get(newType);
		if (control == null) {
			control = createControl(newType);
			controlMap.put(newType, control);
		}
		if (control instanceof AbstractDataForm) {
			AbstractDataForm widget = (AbstractDataForm) control;
			widget.setNewObject(eObj);
			BindingContext bindingContext = widget.getBindingContext();
			if (bindingContext != null) {
				validation = bindingContext.getStatus();
			}
			listener.setValidation(validation);
			if (validation != null) {
				validation.addChangeListener(listener);
			}
		}
		stackLayout.topControl = control;
		stackComp.layout();
	}

	private Control createControl(EClass newType) {
		Control control = null;
		if (newType == null || stackComp.isDisposed()) {
			control = emptyLabel;
		} else if (ApplicationPackageImpl.eINSTANCE.getApplicationElement().isSuperTypeOf(newType)){
			control = DataForms.createWidget(newType, stackComp, null);
		}
		if (control == null) {
			control = emptyLabel;
		}
		return control;
	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		EObject newObj = null;
		if (selection != null && !selection.isEmpty()
				&& selection instanceof IStructuredSelection) {
			Object object = ((IStructuredSelection) selection)
					.getFirstElement();
			if (object instanceof EditPart) {
				Object model = ((EditPart) object).getModel();
				if (model instanceof EObject) {
					newObj = (EObject) model;
				}
			}
		}
		boolean equals = eObj != null ? eObj.equals(newObj) : newObj == null;
		if (!equals) {
			eObj = newObj;
			super.setInput(part, eObj == null ? new StructuredSelection()
					: new StructuredSelection(eObj));
			refresh();
		}
	}

	private class ValidationStatusListener implements IChangeListener {

		private AggregateValidationStatus validation;

		public void handleChange(ChangeEvent event) {
			if (validation != null) {
				IStatus status = (IStatus) validation.getValue();
				setMessage(status);
			}
		}

		public void setValidation(AggregateValidationStatus validation) {
			this.validation = validation;
		}

		public AggregateValidationStatus getValidation() {
			return validation;
		}

	}
}
