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
package org.eclipse.e4.tools.ui.designer.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.e4.tools.ui.designer.utils.ApplicationModelHelper;
import org.eclipse.e4.ui.model.application.impl.ApplicationPackageImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class ElementCreateDialog extends TitleAreaDialog {

	private EClass createType;
	private EObject result;
	private static List<EStructuralFeature> REQUIRED_SF;
	static {
		REQUIRED_SF = new ArrayList<EStructuralFeature>();
		REQUIRED_SF
				.add(ApplicationPackageImpl.Literals.CONTRIBUTION__CONTRIBUTION_URI);
		REQUIRED_SF
				.add(ApplicationPackageImpl.Literals.APPLICATION_ELEMENT__ELEMENT_ID);
	}

	public ElementCreateDialog(Shell parentShell, EClass createType) {
		super(parentShell);
		Assert.isNotNull(createType);
		this.createType = createType;
		this.result = EcoreUtil.create(createType);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Element Create Dialog");
	}

	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		Composite control = new Composite(container, SWT.NONE);
		control.setLayoutData(GridDataFactory.fillDefaults().create());

		int numColumns = 3;
		control.setLayout(new GridLayout(numColumns, false));
		EList<EStructuralFeature> features = createType
				.getEAllStructuralFeatures();
		for (EStructuralFeature sf : features) {
			if (sf instanceof EReference) {
				EReference reference = (EReference) sf;
				if (reference.isContainment()) {
					continue;
				}
			}
			createEditor(control, sf, numColumns);
		}
		setTitle("Create " + createType.getName());
		setMessage("Create and initialize new " + createType.getName()
				+ " instance.");
		return container;
	}

	private void createEditor(Composite parent, EStructuralFeature sf,
			int numColumns) {
		EClassifier eType = sf.getEType();

		Label label = new Label(parent, SWT.NONE);
		if (sf.isRequired()) {
			label.setText(getDisplayName(sf) + " *");
			label.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_RED));
		}
		else {
			label.setText(getDisplayName(sf));
		}

		if (eType instanceof EEnum) {
			Combo combo = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
			EList<EEnumLiteral> eLiterals = ((EEnum) eType).getELiterals();
			for (EEnumLiteral literal : eLiterals) {
				combo.add(literal.getName());
			}
			combo.setLayoutData(GridDataFactory.fillDefaults()
					.grab(true, false).span(numColumns - 1, 1).create());
		} else if (sf.isMany()) {
			Text text = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
			text.setBackground(parent.getDisplay().getSystemColor(
					SWT.COLOR_WHITE));

			text.setLayoutData(GridDataFactory.fillDefaults().grab(true, false)
					.create());
			Button dotButton = new Button(parent, SWT.PUSH);
			dotButton.setText("...");
		} else {
			Text text = new Text(parent, SWT.BORDER);
			ISWTObservableValue observeSource = WidgetProperties.text(
					SWT.Modify).observe(text);
			IObservableValue observeTarget = EMFProperties.value(sf).observe(
					result);
			DataBindingContext context = new DataBindingContext();
			context.bindValue(observeSource, observeTarget);

			text.setLayoutData(GridDataFactory.fillDefaults().grab(true, false)
					.span(numColumns - 1, 1).create());
		}
	}

	private String getDisplayName(EStructuralFeature feature) {
		ComposedAdapterFactory factory = ApplicationModelHelper.getFactory();
		String name = feature.getName();
		IItemPropertySource ps = (IItemPropertySource) factory.adapt(result,
				IItemPropertySource.class);
		String displayName = null;
		if (ps != null) {
			IItemPropertyDescriptor pd = ps.getPropertyDescriptor(result, name);
			if (pd != null) {
				displayName = pd.getDisplayName(feature);
			}
		}
		if (displayName == null) {
			displayName = Character.toUpperCase(name.charAt(0))
					+ name.substring(1);
		}
		return displayName;
	}

	public EObject getResult() {
		return result;
	}
}
