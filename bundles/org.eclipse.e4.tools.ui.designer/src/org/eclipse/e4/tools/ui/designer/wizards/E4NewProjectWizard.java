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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.MApplicationFactory;
import org.eclipse.e4.ui.model.application.MMenu;
import org.eclipse.e4.ui.model.application.MMenuItem;
import org.eclipse.e4.ui.model.application.MPart;
import org.eclipse.e4.ui.model.application.MPartSashContainer;
import org.eclipse.e4.ui.model.application.MPartStack;
import org.eclipse.e4.ui.model.application.MToolBar;
import org.eclipse.e4.ui.model.application.MToolItem;
import org.eclipse.e4.ui.model.application.MWindow;
import org.eclipse.e4.ui.model.application.MWindowTrim;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
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
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;

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

			this.createApplicationResources(fProjectProvider.getProject(),
					new NullProgressMonitor());

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
			String productName = map.get(NewApplicationWizardPage.PRODUCT_NAME);
			String applicationName = map
					.get(NewApplicationWizardPage.APPLICATION);

			extension.setPoint("org.eclipse.core.runtime.products");
			IPluginElement productElement = fmodel.getFactory().createElement(
					extension);

			productElement.setName("product");
			if (applicationName != null) {
				productElement.setAttribute("application", applicationName);
			} else {
				productElement.setAttribute("application",
						NewApplicationWizardPage.E4_APPLICATION);
			}
			productElement.setAttribute("name", productName);

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
			PDEPlugin.logException(e);
		}
	}

	/**
	 * create products extension detail
	 * 
	 * @param project
	 */
	@SuppressWarnings("restriction")
	public void createApplicationResources(IProject project,
			IProgressMonitor monitor) {
		Map<String, String> map = fApplicationPage.getData();
		if (map == null
				|| map.get(NewApplicationWizardPage.PRODUCT_NAME) == null)
			return;

		String projectName = map.get(NewApplicationWizardPage.PRODUCT_NAME);
		String xmiPath = map
				.get(NewApplicationWizardPage.APPLICATION_XMI_PROPERTY);

		if (xmiPath != null && xmiPath.trim().length() > 0) {
			// Create a resource set
			//
			ResourceSet resourceSet = new ResourceSetImpl();

			// Get the URI of the model file.
			//
			URI fileURI = URI.createPlatformResourceURI(project.getName() + "/"
					+ xmiPath, true);

			// Create a resource for this file.
			//
			Resource resource = resourceSet.createResource(fileURI);

			MApplication application = MApplicationFactory.eINSTANCE
					.createApplication();
			resource.getContents().add((EObject) application);

			MWindow mainWindow = MApplicationFactory.eINSTANCE.createWindow();
			application.getChildren().add(mainWindow);
			{
				mainWindow.setLabel(projectName);
				mainWindow.setWidth(500);
				mainWindow.setHeight(400);

				// Menu
				{
					MMenu menu = MApplicationFactory.eINSTANCE.createMenu();
					mainWindow.setMainMenu(menu);

					MMenuItem fileMenuItem = MApplicationFactory.eINSTANCE
							.createMenuItem();
					menu.getChildren().add(fileMenuItem);
					fileMenuItem.setLabel("File");
					{
						MMenuItem menuItemOpen = MApplicationFactory.eINSTANCE
								.createMenuItem();
						fileMenuItem.getChildren().add(menuItemOpen);
						menuItemOpen.setLabel("Open");
						menuItemOpen.setIconURI("platform:/plugin/"
								+ project.getName() + "/icons/sample.gif");

						MMenuItem menuItemSave = MApplicationFactory.eINSTANCE
								.createMenuItem();
						fileMenuItem.getChildren().add(menuItemSave);
						menuItemSave.setLabel("Save");
						menuItemSave.setIconURI("platform:/plugin/"
								+ project.getName() + "/icons/save_edit.gif");

						MMenuItem menuItemQuit = MApplicationFactory.eINSTANCE
								.createMenuItem();
						fileMenuItem.getChildren().add(menuItemQuit);
						menuItemQuit.setLabel("Quit");
					}

					MMenuItem helpMenuItem = MApplicationFactory.eINSTANCE
							.createMenuItem();
					menu.getChildren().add(helpMenuItem);
					helpMenuItem.setLabel("Help");
					{
						MMenuItem menuItemAbout = MApplicationFactory.eINSTANCE
								.createMenuItem();
						helpMenuItem.getChildren().add(menuItemAbout);
						menuItemAbout.setLabel("About");
					}
				}

				// WindowTrim
				{
					MWindowTrim windowTrim = MApplicationFactory.eINSTANCE
							.createWindowTrim();
					mainWindow.getChildren().add(windowTrim);

					MToolBar toolBar = MApplicationFactory.eINSTANCE
							.createToolBar();
					windowTrim.getChildren().add(toolBar);

					MToolItem toolItemOpen = MApplicationFactory.eINSTANCE
							.createToolItem();
					toolBar.getChildren().add(toolItemOpen);
					toolItemOpen.setIconURI("platform:/plugin/"
							+ project.getName() + "/icons/sample.gif");

					MToolItem toolItemSave = MApplicationFactory.eINSTANCE
							.createToolItem();
					toolBar.getChildren().add(toolItemSave);
					toolItemSave.setIconURI("platform:/plugin/"
							+ project.getName() + "/icons/save_edit.gif");
				}

				// Part Container
				MPartSashContainer partSashContainer = MApplicationFactory.eINSTANCE
						.createPartSashContainer();
				mainWindow.getChildren().add(partSashContainer);
				MPartStack partStack = MApplicationFactory.eINSTANCE
						.createPartStack();
				partSashContainer.getChildren().add(partStack);

				MPart part = MApplicationFactory.eINSTANCE.createPart();
				partStack.getChildren().add(part);
				part.setLabel("Main");
			}
			Map<Object, Object> options = new HashMap<Object, Object>();
			options.put(XMLResource.OPTION_ENCODING, "UTF-8");
			try {
				resource.save(options);
			} catch (IOException e) {
				PDEPlugin.logException(e);
			}
		}

		String cssPath = map
				.get(NewApplicationWizardPage.APPLICATION_CSS_PROPERTY);
		if (cssPath != null && cssPath.trim().length() > 0) {
			IFile file = project.getFile(cssPath);

			try {
				prepareFolder(file.getParent(), monitor);
				file.create(new ByteArrayInputStream(new byte[0]), true,
						monitor);
			} catch (CoreException e) {
				PDEPlugin.logException(e);
			}
		}

		IFolder folder = project.getFolder("icons");
		try {
			folder.create(true, true, monitor);
			Bundle bundle = Platform
					.getBundle("org.eclipse.e4.tools.ui.designer");

			for (String fileName : new String[] { "sample.gif", "save_edit.gif" }) {
				URL sampleUrl = bundle.getEntry("resources/icons/" + fileName);
				sampleUrl = FileLocator.resolve(sampleUrl);
				InputStream inputStream = sampleUrl.openStream();
				IFile file = folder.getFile(fileName);
				file.create(inputStream, true, monitor);
			}
		} catch (Exception e) {
			PDEPlugin.logException(e);
		}
	}

	private void prepareFolder(IContainer container, IProgressMonitor monitor)
			throws CoreException {
		IContainer parent = container.getParent();
		if (parent instanceof IFolder) {
			prepareFolder((IFolder) parent, monitor);
		}
		if (!container.exists() && container instanceof IFolder) {
			IFolder folder = (IFolder) container;
			folder.create(true, true, monitor);
		}
	}

	public String getPluginId() {
		return fPluginData.getId();
	}

	public String getPluginVersion() {
		return fPluginData.getVersion();
	}
}
