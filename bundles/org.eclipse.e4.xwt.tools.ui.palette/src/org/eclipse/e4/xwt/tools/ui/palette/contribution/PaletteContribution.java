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
package org.eclipse.e4.xwt.tools.ui.palette.contribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.xwt.tools.ui.palette.CompoundInitializer;
import org.eclipse.e4.xwt.tools.ui.palette.Entry;
import org.eclipse.e4.xwt.tools.ui.palette.Initializer;
import org.eclipse.e4.xwt.tools.ui.palette.PaletteFactory;
import org.eclipse.e4.xwt.tools.ui.palette.page.CustomPalettePage;
import org.eclipse.e4.xwt.tools.ui.palette.page.CustomPaletteViewerProvider;
import org.eclipse.e4.xwt.tools.ui.palette.page.resources.IPaletteResourceProvider;
import org.eclipse.e4.xwt.tools.ui.palette.root.PaletteRootFactory;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.Tool;
import org.eclipse.gef.palette.PaletteRoot;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class PaletteContribution implements IExecutableExtension {

	public static final String EXTENSION_POINT_ID = "org.eclipse.e4.xwt.tools.ui.palette.paletteContribution";
	public static final String CONTRIBUTION = "Contribution";
	public static final String CONTRIBUTION_TARGET_ID = "targetId";
	public static final String RESOURCE = "Resource";
	public static final String RESOURCE_URI = "uri";
	public static final String RESOURCE_PROVIDER = "provider";
	public static final String INITIALIZER = "Initializer";
	public static final String INITIALIZER_TARGET = "target";
	public static final String INITIALIZER_TARGET_GLOBAL = "*";
	public static final String INITIALIZER_CLASS = "class";
	public static final String TOOL = "Tool";
	public static final String TOOL_CLASS = "class";
	public static final String TOOL_TYPE = "type";
	public static final String TOOL_TYPE_CREATION = "creation";
	public static final String TOOL_TYPE_SELECTION = "selection";

	private String editorId;
	private Resource resource;
	private String resourceURI;
	private Map<String, List<Initializer>> initializersMap;
	private List<IPaletteResourceProvider> resourceProviders;

	private Class<? extends Tool> creationTool;
	private Class<? extends Tool> selectionTool;

	private static final Map<String, PaletteContribution> contributions = new HashMap<String, PaletteContribution>();

	private PaletteContribution(String editorId) {
		this.editorId = editorId;
		loadFromExtensions();
	}

	private void loadFromExtensions() {
		IConfigurationElement[] configurations = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(EXTENSION_POINT_ID);
		for (IConfigurationElement ctrib : configurations) {
			String targetId = ctrib.getAttribute(CONTRIBUTION_TARGET_ID);
			if (!editorId.equals(targetId)) {
				continue;
			}
			loadResources(ctrib.getChildren(RESOURCE));
			loadInitializers(ctrib.getChildren(INITIALIZER));
			loadTools(ctrib.getChildren(TOOL));
		}
	}

	private void loadResources(IConfigurationElement[] resources) {
		if (resources == null || resources.length == 0) {
			return;
		}
		if (resourceProviders == null) {
			resourceProviders = new ArrayList<IPaletteResourceProvider>();
		}
		for (IConfigurationElement resConfig : resources) {
			String uri = resConfig.getAttribute(RESOURCE_URI);
			try {
				IPaletteResourceProvider provider = (IPaletteResourceProvider) resConfig
						.createExecutableExtension(RESOURCE_PROVIDER);
				resourceProviders.add(provider);
			} catch (CoreException e) {
			}
		}
	}

	private void loadInitializers(IConfigurationElement[] initializers) {
		if (initializers == null || initializers.length == 0) {
			return;
		}
		if (initializersMap == null) {
			initializersMap = new HashMap<String, List<Initializer>>();
		}
		for (IConfigurationElement initConfig : initializers) {
			String target = initConfig.getAttribute(INITIALIZER_TARGET);
			if (target == null || "".equals(target)) {
				target = INITIALIZER_TARGET_GLOBAL;
			}
			List<Initializer> inits = initializersMap.get(target);
			if (inits == null) {
				initializersMap.put(target, inits = new ArrayList<Initializer>());
			}
			try {
				Initializer initializer = (Initializer) initConfig
						.createExecutableExtension(INITIALIZER_CLASS);
				inits.add(initializer);
			} catch (CoreException e) {
				continue;
			}
		}
	}

	private void loadTools(IConfigurationElement[] tools) {
		if (tools == null || tools.length == 0) {
			return;
		}
		for (IConfigurationElement toolConfig : tools) {
			String type = toolConfig.getAttribute(TOOL_TYPE);
			try {
				Tool tool = (Tool) toolConfig.createExecutableExtension(TOOL_CLASS);
				if (TOOL_TYPE_CREATION.equals(type)) {
					creationTool = tool.getClass();
				} else if (TOOL_TYPE_SELECTION.equals(type)) {
					selectionTool = tool.getClass();
				}
			} catch (CoreException e) {
				continue;
			}
		}
	}

	public Class<? extends Tool> getCreationTool() {
		return creationTool;
	}

	public Class<? extends Tool> getSelectionTool() {
		return selectionTool;
	}

	public String getEditorId() {
		return editorId;
	}

	public Initializer getInitializer(String type) {
		if (initializersMap == null) {
			return null;
		}
		if (type == null || "".equals(type)) {
			type = INITIALIZER_TARGET_GLOBAL;
		}
		List<Initializer> list = initializersMap.get(type);
		if (list == null || list.isEmpty()) {
			return null;
		}
		CompoundInitializer initializer = PaletteFactory.eINSTANCE.createCompoundInitializer();
		initializer.getInitializers().addAll(list);
		return initializer.unwrap();
	}

	public void applyInitializer(Entry entry) {
		if (entry == null) {
			return;
		}
		CompoundInitializer initializer = PaletteFactory.eINSTANCE.createCompoundInitializer();

		// get from NAME.
		String name = entry.getName();
		List<Initializer> list = initializersMap.get(name);
		EList<Initializer> initializers = initializer.getInitializers();
		if (list != null && !list.isEmpty()) {
			initializers.addAll(list);
		}
		// get from ID.
		String id = entry.getId();
		list = initializersMap.get(id);
		if (list != null && !list.isEmpty()) {
			initializers.addAll(list);
		}
		// add old initializer.
		Initializer oldInitializer = entry.getInitializer();
		if (oldInitializer != null) {
			initializers.add(oldInitializer);
		}
		Initializer globalInitializer = getGlobalInitializer();
		if (initializers.isEmpty() && globalInitializer != null) {
			initializers.add(oldInitializer);
		}
		if (!initializers.isEmpty()) {
			entry.setInitializer(initializer.unwrap());
		}
	}

	public Initializer getGlobalInitializer() {
		return getInitializer(INITIALIZER_TARGET_GLOBAL);
	}

	public boolean hasInitialiers() {
		return initializersMap != null && initializersMap.size() > 0;
	}

	public String getResourceURI() {
		return resourceURI;
	}

	public Resource getResource() {
		return resource;
	}

	public List<IPaletteResourceProvider> getResourceProviders() {
		return resourceProviders;
	}

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {

	}

	public CustomPalettePage createPalette(String editorId, EditDomain editDomain) {
		PaletteRootFactory factory = new PaletteRootFactory(resourceProviders, creationTool,
				selectionTool);
		PaletteRoot paletteRoot = factory.createPaletteRoot();
		if (paletteRoot != null) {
			editDomain.setPaletteRoot(paletteRoot);
		}
		CustomPaletteViewerProvider provider = new CustomPaletteViewerProvider(editDomain);
		return new CustomPalettePage(provider);
	}

	public static PaletteContribution getContribution(String editorId) {
		PaletteContribution paletteContribution = contributions.get(editorId);
		if (paletteContribution == null) {
			paletteContribution = new PaletteContribution(editorId);
			contributions.put(editorId, paletteContribution);
		}
		return paletteContribution;
	}
}
