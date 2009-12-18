/*******************************************************************************
 * Copyright (c) 2006, 2009 Soyatec(http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.ui.designer.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.internal.core.ICoreConstants;
import org.eclipse.pde.internal.core.bundle.WorkspaceBundlePluginModel;
import org.eclipse.pde.internal.core.plugin.WorkspacePluginModelBase;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.pde.internal.ui.PDEUIMessages;
import org.eclipse.pde.internal.ui.wizards.IProjectProvider;
import org.eclipse.pde.internal.ui.wizards.plugin.NewPluginProjectWizard;
import org.eclipse.pde.internal.ui.wizards.plugin.NewProjectCreationOperation;
import org.eclipse.pde.internal.ui.wizards.plugin.NewProjectCreationPage;
import org.eclipse.pde.internal.ui.wizards.plugin.PluginContentPage;
import org.eclipse.pde.internal.ui.wizards.plugin.PluginFieldData;
import org.eclipse.ui.IWorkingSet;

/**
 * @author jin.liu (jin.liu@soyatec.com)
 */
public class E4NewProjectWizard extends NewPluginProjectWizard {

	private PluginFieldData fPluginData;
	private NewApplicationWizardPage fApplicationPage;
	private IProjectProvider fProjectProvider;

	public E4NewProjectWizard() {
		fPluginData = new PluginFieldData();
	}

	public void addPages() {
		fMainPage = new NewProjectCreationPage(
				"main", fPluginData, false, getSelection()); //$NON-NLS-1$
		fMainPage.setTitle(PDEUIMessages.NewProjectWizard_MainPage_title);
		fMainPage.setDescription(PDEUIMessages.NewProjectWizard_MainPage_desc);
		String pname = getDefaultValue(DEF_PROJECT_NAME);
		if (pname != null)
			fMainPage.setInitialProjectName(pname);
		addPage(fMainPage);

		fProjectProvider = new IProjectProvider() {
			public String getProjectName() {
				return fMainPage.getProjectName();
			}

			public IProject getProject() {
				return fMainPage.getProjectHandle();
			}

			public IPath getLocationPath() {
				return fMainPage.getLocationPath();
			}
		};

		fContentPage = new PluginContentPage(
				"page2", fProjectProvider, fMainPage, fPluginData); //$NON-NLS-1$

		fApplicationPage = new NewApplicationWizardPage(fProjectProvider);

		addPage(fContentPage);
		addPage(fApplicationPage);
	}

	@SuppressWarnings("restriction")
	public boolean performFinish() {
		try {
			fMainPage.updateData();
			fContentPage.updateData();
			IDialogSettings settings = getDialogSettings();
			if (settings != null) {
				fMainPage.saveSettings(settings);
				fContentPage.saveSettings(settings);
			}
			getContainer().run(
					false,
					true,
					new NewProjectCreationOperation(fPluginData,
							fProjectProvider, null));

			IWorkingSet[] workingSets = fMainPage.getSelectedWorkingSets();
			if (workingSets.length > 0)
				getWorkbench().getWorkingSetManager().addToWorkingSets(
						fProjectProvider.getProject(), workingSets);

			this.createProductsExtension(fProjectProvider.getProject());

			return true;
		} catch (InvocationTargetException e) {
			PDEPlugin.logException(e);
		} catch (InterruptedException e) {
		}
		return false;
	}

	/**
	 * create products extension detail
	 * 
	 * @param project
	 */
	@SuppressWarnings("restriction")
	public void createProductsExtension(IProject project) {
		Map<String, String> map = fApplicationPage.getData();
		if (map == null
				|| map.get(NewApplicationWizardPage.PRODUCT_NAME) == null)
			return;

		WorkspacePluginModelBase fmodel = new WorkspaceBundlePluginModel(
				project.getFile(ICoreConstants.BUNDLE_FILENAME_DESCRIPTOR),
				project.getFile(ICoreConstants.PLUGIN_FILENAME_DESCRIPTOR));
		IPluginExtension extension = fmodel.getFactory().createExtension();
		try {
			extension.setPoint("org.eclipse.core.runtime.products");
			extension.setId("product");
			IPluginElement productElement = fmodel.getFactory().createElement(
					extension);
			productElement.setName("product");
			productElement.setAttribute("application", map
					.get(NewApplicationWizardPage.APPLICATION) == null ? ""
					: map.get(NewApplicationWizardPage.APPLICATION));
			productElement.setAttribute("name", map
					.get(NewApplicationWizardPage.APPLICATION));

			Set<Entry<String, String>> set = map.entrySet();
			if (set != null) {
				Iterator<Entry<String, String>> it = set.iterator();
				if (it != null) {
					while (it.hasNext()) {
						Entry<String, String> entry = it.next();
						if (entry.getKey().equals(
								NewApplicationWizardPage.PRODUCT_NAME)
								|| entry.getKey().equals(
										NewApplicationWizardPage.APPLICATION)) {
							continue;
						}
						IPluginElement element = fmodel.getFactory()
								.createElement(productElement);
						element.setName("property");
						element.setAttribute("name", entry.getKey());
						element.setAttribute("value", entry.getValue());
						productElement.add(element);
					}
				}
			}
			extension.add(productElement);
			fmodel.getPluginBase().add(extension);
			fmodel.save();

		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public String getPluginId() {
		return fPluginData.getId();
	}

	public String getPluginVersion() {
		return fPluginData.getVersion();
	}
}
