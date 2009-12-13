package org.eclipse.e4.tools.ui.designer.render;

import java.io.InputStream;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.IRegistryEventListener;

public class DecoratedExtensionRegistry implements IExtensionRegistry {
	protected IExtensionRegistry extensionRegistry;

	public DecoratedExtensionRegistry(IExtensionRegistry extensionRegistry) {
		this.extensionRegistry = extensionRegistry;
	}
	
	public void addRegistryChangeListener(IRegistryChangeListener listener,
			String namespace) {
		extensionRegistry.addRegistryChangeListener(listener, namespace);
	}

	public void addRegistryChangeListener(IRegistryChangeListener listener) {
		extensionRegistry.addRegistryChangeListener(listener);
	}

	public IConfigurationElement[] getConfigurationElementsFor(
			String extensionPointId) {
		IConfigurationElement[] elements = extensionRegistry.getConfigurationElementsFor(extensionPointId);
		if ("org.eclipse.e4.workbench.rendererfactory".equals(extensionPointId)) {
			for (IConfigurationElement element : elements) {
				String className = element.getAttribute("class");
				if ("org.eclipse.e4.tools.ui.designer.render.DesignerWorkbenchRendererFactory".equals(className)) {
					return new IConfigurationElement[] {element};
				}
			}
			throw new IllegalStateException("Designer Factoring is not found.");
		}
		return elements;
	}

	public IConfigurationElement[] getConfigurationElementsFor(
			String namespace, String extensionPointName) {
		return extensionRegistry.getConfigurationElementsFor(namespace,
				extensionPointName);
	}

	public IConfigurationElement[] getConfigurationElementsFor(
			String namespace, String extensionPointName, String extensionId) {		
		return extensionRegistry.getConfigurationElementsFor(namespace,
				extensionPointName, extensionId);
	}

	public IExtension getExtension(String extensionId) {
		return extensionRegistry.getExtension(extensionId);
	}

	public IExtension getExtension(String extensionPointId, String extensionId) {
		return extensionRegistry.getExtension(extensionPointId, extensionId);
	}

	public IExtension getExtension(String namespace, String extensionPointName,
			String extensionId) {
		return extensionRegistry.getExtension(namespace, extensionPointName,
				extensionId);
	}

	public IExtensionPoint getExtensionPoint(String extensionPointId) {
		return extensionRegistry.getExtensionPoint(extensionPointId);
	}

	public IExtensionPoint getExtensionPoint(String namespace,
			String extensionPointName) {
		return extensionRegistry.getExtensionPoint(namespace,
				extensionPointName);
	}

	public IExtensionPoint[] getExtensionPoints() {
		return extensionRegistry.getExtensionPoints();
	}

	public IExtensionPoint[] getExtensionPoints(String namespace) {
		return extensionRegistry.getExtensionPoints(namespace);
	}

	public IExtensionPoint[] getExtensionPoints(IContributor contributor) {
		return extensionRegistry.getExtensionPoints(contributor);
	}

	public IExtension[] getExtensions(String namespace) {
		return extensionRegistry.getExtensions(namespace);
	}

	public IExtension[] getExtensions(IContributor contributor) {
		return extensionRegistry.getExtensions(contributor);
	}

	public String[] getNamespaces() {
		return extensionRegistry.getNamespaces();
	}

	public void removeRegistryChangeListener(IRegistryChangeListener listener) {
		extensionRegistry.removeRegistryChangeListener(listener);
	}

	public boolean addContribution(InputStream is, IContributor contributor,
			boolean persist, String name, ResourceBundle translationBundle,
			Object token) throws IllegalArgumentException {
		return extensionRegistry.addContribution(is, contributor, persist,
				name, translationBundle, token);
	}

	public boolean removeExtension(IExtension extension, Object token)
			throws IllegalArgumentException {
		return extensionRegistry.removeExtension(extension, token);
	}

	public boolean removeExtensionPoint(IExtensionPoint extensionPoint,
			Object token) throws IllegalArgumentException {
		return extensionRegistry.removeExtensionPoint(extensionPoint, token);
	}

	public void stop(Object token) throws IllegalArgumentException {
		extensionRegistry.stop(token);
	}

	public void addListener(IRegistryEventListener listener) {
		extensionRegistry.addListener(listener);
	}

	public void addListener(IRegistryEventListener listener,
			String extensionPointId) {
		extensionRegistry.addListener(listener, extensionPointId);
	}

	public void removeListener(IRegistryEventListener listener) {
		extensionRegistry.removeListener(listener);
	}

	public boolean isMultiLanguage() {
		return extensionRegistry.isMultiLanguage();
	}
}
