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
package org.eclipse.e4.tools.ui.designer.wizards;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.internal.ui.wizards.IProjectProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.branding.IProductConstants;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author jin.liu (jin.liu@soyatec.com)
 */
public class NewApplicationWizardPage extends WizardPage {

	private static final String APPLICATION_XMI_PROPERTY = "applicationXMI";
	private static final String APPLICATION_CSS_PROPERTY = "applicationCSS";
	private static String[] PROPERTIES = new String[] {
			IProductConstants.APP_NAME, APPLICATION_XMI_PROPERTY,
			APPLICATION_CSS_PROPERTY, IProductConstants.ABOUT_TEXT,
			IProductConstants.STARTUP_FOREGROUND_COLOR,
			IProductConstants.STARTUP_MESSAGE_RECT,
			IProductConstants.STARTUP_PROGRESS_RECT,
			IProductConstants.PREFERENCE_CUSTOMIZATION };

	private final Map<String, String> data;
	private IProject project;
	private IProjectProvider projectProvider;

	protected NewApplicationWizardPage(IProjectProvider projectProvider) {
		super("New Application Wizard Page");
		this.projectProvider = projectProvider;
		data = new HashMap<String, String>();
		setTitle("Application");
		setMessage("Configure application with special values.");
	}

	public IProject getProject() {
		if (project == null && projectProvider != null) {
			project = projectProvider.getProject();
		}
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createControl(Composite parent) {
		Composite control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout());

		Group productGroup = createProductGroup(control);
		productGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Group propertyGroup = createPropertyGroup(control);
		propertyGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		setControl(control);
	}

	private Group createPropertyGroup(Composite control) {
		Group group = new Group(control, SWT.NONE);
		group.setText("Properties");

		group.setLayout(new GridLayout(2, false));

		for (String property : PROPERTIES) {
			createPropertyItem(group, property);
		}

		return group;
	}

	private void createPropertyItem(Composite parent, final String property) {
		Hyperlink propertyLink = new Hyperlink(parent, SWT.NONE);
		propertyLink.setText(property);
		propertyLink.setUnderlined(true);

		final Text valueText = new Text(parent, SWT.BORDER);
		valueText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		propertyLink.addHyperlinkListener(new IHyperlinkListener() {
			public void linkExited(HyperlinkEvent e) {

			}

			public void linkEntered(HyperlinkEvent e) {

			}

			public void linkActivated(HyperlinkEvent e) {
				handleLinkEvent(property, valueText);
			}
		});

		valueText.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event event) {
				handleTextEvent(property, valueText);
			}
		});
	}

	private void handleLinkEvent(String property, Text valueText) {
		if (property == null || valueText == null || valueText.isDisposed()) {
			return;
		}
		if (property.equals(APPLICATION_XMI_PROPERTY)) {
			valueText.setText("Application.xmi");
		} else if (property.equals(APPLICATION_CSS_PROPERTY)) {
			valueText.setText("css/default.css");
		} else if (property.equals(IProductConstants.APP_NAME)) {
			valueText.setText("New Product");
		} else if (property.equals(IProductConstants.ABOUT_TEXT)) {
			valueText.setText("About Product.");
		} else if (property.equals(IProductConstants.PREFERENCE_CUSTOMIZATION)) {
			valueText.setText("plugin_customization.ini");
		} else if (property.equals(IProductConstants.STARTUP_FOREGROUND_COLOR)) {
			valueText.setText("FFFFFF");
		} else if (property.equals(IProductConstants.STARTUP_MESSAGE_RECT)) {
			valueText.setText("7,252,445,20");
		} else if (property.equals(IProductConstants.STARTUP_PROGRESS_RECT)) {
			valueText.setText("5,275,445,15");
		}
	}

	private void handleTextEvent(String property, Text valueText) {
		if (property == null || valueText == null || valueText.isDisposed()) {
			return;
		}
		String value = valueText.getText();
		if (value.equals("")) {
			value = null;
		}
		data.put(property, value);
	}

	private Group createProductGroup(Composite control) {
		Group proGroup = new Group(control, SWT.NONE);
		proGroup.setText("Product");

		proGroup.setLayout(new GridLayout(2, false));

		Label proNameLabel = new Label(proGroup, SWT.NONE);
		proNameLabel.setText("Name*");

		Text proNameText = new Text(proGroup, SWT.BORDER);
		proNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label proApplicationLabel = new Label(proGroup, SWT.NONE);
		proApplicationLabel.setText("Application");

		Text proApplicationText = new Text(proGroup, SWT.BORDER);
		proApplicationText
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return proGroup;
	}

}
