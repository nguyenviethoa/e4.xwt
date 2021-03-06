/*******************************************************************************
 * Copyright (c) 2006, 2010 Soyatec(http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.ui.designer.wizards;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.MApplicationFactory;
import org.eclipse.e4.ui.model.application.commands.MBindingContext;
import org.eclipse.e4.ui.model.application.commands.MBindingTable;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MCommandsFactory;
import org.eclipse.e4.ui.model.application.commands.MHandler;
import org.eclipse.e4.ui.model.application.commands.MKeyBinding;
import org.eclipse.e4.ui.model.application.ui.advanced.MAdvancedFactory;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainer;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimBar;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginImport;
import org.eclipse.pde.internal.core.ICoreConstants;
import org.eclipse.pde.internal.core.bundle.WorkspaceBundlePluginModel;
import org.eclipse.pde.internal.core.plugin.WorkspacePluginModelBase;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.pde.internal.ui.PDEUIMessages;
import org.eclipse.pde.internal.ui.wizards.IProjectProvider;
import org.eclipse.pde.internal.ui.wizards.plugin.NewPluginProjectWizard;
import org.eclipse.pde.internal.ui.wizards.plugin.NewProjectCreationOperation;
import org.eclipse.pde.internal.ui.wizards.plugin.PluginFieldData;
import org.eclipse.ui.IWorkingSet;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

/**
 * @author jin.liu (jin.liu@soyatec.com)
 */
public class E4NewProjectWizard extends NewPluginProjectWizard {

	private PluginFieldData fPluginData;
	private NewApplicationWizardPage fApplicationPage;
	private IProjectProvider fProjectProvider;
	private PluginContentPage fContentPage;

	public E4NewProjectWizard() {
		fPluginData = new PluginFieldData();
	}

	public void addPages() {
		fMainPage = new E4NewProjectWizardPage(
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
							fProjectProvider, null) {
						private WorkspacePluginModelBase model;

						@Override
						protected void adjustManifests(
								IProgressMonitor monitor, IProject project,
								IPluginBase bundle) throws CoreException {
							super.adjustManifests(monitor, project, bundle);
							IPluginBase pluginBase = model.getPluginBase();
							String[] dependencyId = new String[] {
									"javax.inject",
									"org.eclipse.core.resources",
									"org.eclipse.core.runtime",
									"org.eclipse.swt",
									"org.eclipse.core.databinding",
									"org.eclipse.core.databinding.beans",
									"org.eclipse.jface",
									"org.eclipse.jface.databinding",
									"org.eclipse.e4.ui.services",
									"org.eclipse.e4.ui.workbench",
									"org.eclipse.e4.core.services",
									"org.eclipse.e4.core.di",
									"org.eclipse.e4.core.contexts",
									"org.eclipse.e4.ui.workbench.swt",
									"org.eclipse.core.databinding.property",
									"org.eclipse.e4.ui.css.core",
									"org.w3c.css.sac",
									"org.eclipse.e4.core.commands",
									"org.eclipse.e4.ui.bindings" };
							for (String id : dependencyId) {
								Bundle dependency = Platform.getBundle(id);

								IPluginImport iimport = model
										.getPluginFactory().createImport();
								iimport.setId(id);
								Version version = dependency.getVersion();
								String versionString = version.getMajor() + "."
										+ version.getMinor() + "."
										+ version.getMicro();
								iimport.setVersion(versionString);
								pluginBase.add(iimport);
							}
						}

						@Override
						protected void setPluginLibraries(
								WorkspacePluginModelBase model)
								throws CoreException {
							this.model = model;
							super.setPluginLibraries(model);
						}
					});

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

			String xmiPath = map
					.get(NewApplicationWizardPage.APPLICATION_XMI_PROPERTY);
			if (xmiPath != null) {
				xmiPath = productName + "/" + xmiPath;
				map.put(NewApplicationWizardPage.APPLICATION_XMI_PROPERTY,
						xmiPath);
			}
			String cssValue = map
					.get(NewApplicationWizardPage.APPLICATION_CSS_PROPERTY);
			if (cssValue != null) {
				cssValue = "platform:/plugin/" + productName + "/" + cssValue;
				map.put(NewApplicationWizardPage.APPLICATION_CSS_PROPERTY,
						cssValue);
			}

			extension.setPoint("org.eclipse.core.runtime.products");
			extension.setId("product");
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
						String value = entry.getValue();
						if (value == null || value.trim().length() == 0) {
							continue;
						}

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
						element.setAttribute("value", value);
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

		IJavaProject javaProject = JavaCore.create(project);
		IPackageFragment fragment = null;
		try {
			for (IPackageFragment element : javaProject.getPackageFragments()) {
				if (element.getKind() == IPackageFragmentRoot.K_SOURCE) {
					fragment = element;
				}
			}
		} catch (JavaModelException e1) {
			e1.printStackTrace();
		}

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

			MApplication application = MApplicationFactory.INSTANCE
					.createApplication();
			
			application.setElementId("org.eclipse.e4.ide.application");
			
			MBindingContext rootContext = MCommandsFactory.INSTANCE.createBindingContext();
			rootContext.setElementId("org.eclipse.ui.contexts.dialogAndWindow");
			rootContext.setName("In Dialog and Windows");
			
			MBindingContext childContext = MCommandsFactory.INSTANCE.createBindingContext();
			childContext.setElementId("org.eclipse.ui.contexts.window");
			childContext.setName("In Windows");
			rootContext.getChildren().add(childContext);
			
			childContext = MCommandsFactory.INSTANCE.createBindingContext();
			childContext.setElementId("org.eclipse.ui.contexts.dialog");
			childContext.setName("In Dialogs");
			rootContext.getChildren().add(childContext);
			
			application.getRootContext().add(rootContext);
			application.getBindingContexts().add(rootContext);
			
			resource.getContents().add((EObject) application);

			// Create Quit command
			MCommand quitCommand = createCommand("quitCommand", "QuitHandler",
					"Ctrl+Q", projectName, fragment, application);

			MCommand openCommand = createCommand("openCommand", "OpenHandler",
					"Ctrl+O", projectName, fragment, application);

			MCommand saveCommand = createCommand("saveCommand", "SaveHandler",
					"Ctrl+S", projectName, fragment, application);

			MCommand aboutCommand = createCommand("aboutCommand",
					"AboutHandler", "Ctrl+A", projectName, fragment,
					application);

			MTrimmedWindow mainWindow = MBasicFactory.INSTANCE.createTrimmedWindow();
			application.getChildren().add(mainWindow);
			{
				mainWindow.setLabel(projectName);
				mainWindow.setWidth(500);
				mainWindow.setHeight(400);

				// Menu
				{
					MMenu menu = MMenuFactory.INSTANCE.createMenu();
					mainWindow.setMainMenu(menu);
					menu.setElementId("menu:org.eclipse.ui.main.menu");

					MMenu fileMenuItem = MMenuFactory.INSTANCE
							.createMenu();
					menu.getChildren().add(fileMenuItem);
					fileMenuItem.setLabel("File");
					{
						MHandledMenuItem menuItemOpen = MMenuFactory.INSTANCE
								.createHandledMenuItem();
						fileMenuItem.getChildren().add(menuItemOpen);
						menuItemOpen.setLabel("Open");
						menuItemOpen.setIconURI("platform:/plugin/"
								+ project.getName() + "/icons/sample.gif");
						menuItemOpen.setCommand(openCommand);

						MHandledMenuItem menuItemSave = MMenuFactory.INSTANCE
								.createHandledMenuItem();
						fileMenuItem.getChildren().add(menuItemSave);
						menuItemSave.setLabel("Save");
						menuItemSave.setIconURI("platform:/plugin/"
								+ project.getName() + "/icons/save_edit.gif");
						menuItemSave.setCommand(saveCommand);

						MHandledMenuItem menuItemQuit = MMenuFactory.INSTANCE
								.createHandledMenuItem();
						fileMenuItem.getChildren().add(menuItemQuit);
						menuItemQuit.setLabel("Quit");
						menuItemQuit.setCommand(quitCommand);
					}
					MMenu helpMenuItem = MMenuFactory.INSTANCE
							.createMenu();
					menu.getChildren().add(helpMenuItem);
					helpMenuItem.setLabel("Help");
					{
						MHandledMenuItem menuItemAbout = MMenuFactory.INSTANCE
								.createHandledMenuItem();
						helpMenuItem.getChildren().add(menuItemAbout);
						menuItemAbout.setLabel("About");
						menuItemAbout.setCommand(aboutCommand);
					}
				}

				// PerspectiveStack
				{
					MPerspectiveStack perspectiveStack = MAdvancedFactory.INSTANCE
							.createPerspectiveStack();
					mainWindow.getChildren().add(perspectiveStack);

					MPerspective perspective = MAdvancedFactory.INSTANCE
							.createPerspective();
					perspectiveStack.getChildren().add(perspective);
					{
						// Part Container
						MPartSashContainer partSashContainer = MBasicFactory.INSTANCE
								.createPartSashContainer();
						perspective.getChildren().add(partSashContainer);

						MPartStack partStack = MBasicFactory.INSTANCE
								.createPartStack();
						partSashContainer.getChildren().add(partStack);
//
//						MPart part = MApplicationFactory.eINSTANCE.createPart();
//						partStack.getChildren().add(part);
//						part.setLabel("Main");
					}

					// WindowTrim
					{
						MTrimBar trimBar = MBasicFactory.INSTANCE.createTrimBar();
						mainWindow.getTrimBars().add(trimBar);

						MToolBar toolBar = MMenuFactory.INSTANCE
								.createToolBar();
						toolBar.setElementId("toolbar:org.eclipse.ui.main.toolbar");
						trimBar.getChildren().add(toolBar);

						MHandledToolItem toolItemOpen = MMenuFactory.INSTANCE
								.createHandledToolItem();
						toolBar.getChildren().add(toolItemOpen);
						toolItemOpen.setIconURI("platform:/plugin/"
								+ project.getName() + "/icons/sample.gif");
						toolItemOpen.setCommand(openCommand);

						MHandledToolItem toolItemSave = MMenuFactory.INSTANCE
								.createHandledToolItem();
						toolBar.getChildren().add(toolItemSave);
						toolItemSave.setIconURI("platform:/plugin/"
								+ project.getName() + "/icons/save_edit.gif");
						toolItemSave.setCommand(saveCommand);
					}
				}
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

				URL corePath = ResourceLocator
						.getProjectTemplateFiles("css/default.css");
				file.create(corePath.openStream(), true, monitor);
			} catch (Exception e) {
				PDEPlugin.logException(e);
			}
		}

		// IFolder folder = project.getFolder("icons");
		// try {
		// folder.create(true, true, monitor);
		// Bundle bundle = Platform
		// .getBundle("org.eclipse.e4.tools.ui.designer");
		//
		// for (String fileName : new String[] { "sample.gif", "save_edit.gif"
		// }) {
		// URL sampleUrl = bundle.getEntry("resources/icons/" + fileName);
		// sampleUrl = FileLocator.resolve(sampleUrl);
		// InputStream inputStream = sampleUrl.openStream();
		// IFile file = folder.getFile(fileName);
		// file.create(inputStream, true, monitor);
		// }
		// } catch (Exception e) {
		// PDEPlugin.logException(e);
		// }

		String template_id = "common";
		Set<String> binaryExtentions = new HashSet<String>();
		binaryExtentions.add(".gif");
		binaryExtentions.add(".png");

		Map<String, String> keys = new HashMap<String, String>();
		keys.put("projectName", projectName);
		keys.put("packageName", fragment.getElementName() + ".handlers");

		try {
			URL corePath = ResourceLocator.getProjectTemplateFiles(template_id);
			IRunnableWithProgress op = new TemplateOperation(corePath, project,
					keys, binaryExtentions);
			getContainer().run(false, true, op);
		} catch (Exception e) {
			PDEPlugin.logException(e);
		}

		try {
			URL corePath = ResourceLocator.getProjectTemplateFiles("src");
			IRunnableWithProgress op = new TemplateOperation(corePath,
					(IContainer) fragment.getResource(), keys, binaryExtentions);
			getContainer().run(false, true, op);
		} catch (Exception e) {
			PDEPlugin.logException(e);
		}
	}

	private MCommand createCommand(String name, String className,
			String keyBinding, String projectName, IPackageFragment fragment,
			MApplication application) {
		MCommand command = MCommandsFactory.INSTANCE.createCommand();
		command.setCommandName(name);
		command.setElementId(projectName + "." + name);
		application.getCommands().add(command);
		{
			// Create Quit handler for command
			MHandler quitHandler =MCommandsFactory.INSTANCE
					.createHandler();
			quitHandler.setCommand(command);
			quitHandler.setContributionURI("platform:/plugin/" + projectName + "/"
					+ fragment.getElementName() + ".handlers." + className);
			application.getHandlers().add(quitHandler);

			MKeyBinding binding = MCommandsFactory.INSTANCE
					.createKeyBinding();
			binding.setKeySequence(keyBinding);
			binding.setCommand(command);
			List<MBindingTable> tables = application.getBindingTables();
			if (tables.size()==0) {
				MBindingContext rootContext = null;
				if (application.getRootContext().size()>0) {
					rootContext = application.getRootContext().get(0);
				} else {
					rootContext = MCommandsFactory.INSTANCE.createBindingContext();
					rootContext.setElementId("org.eclipse.ui.contexts.dialogAndWindow");
					rootContext.setName("In Dialog and Windows");
					application.getRootContext().add(rootContext);
				}
				
				MBindingTable table = MCommandsFactory.INSTANCE.createBindingTable();
				table.setBindingContext(rootContext);
				tables.add(table);
			}
			tables.get(0).getBindings().add(binding);
		}
		return command;
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
