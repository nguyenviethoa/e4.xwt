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
package org.eclipse.e4.tools.ui.designer.utils;

import org.eclipse.e4.ui.workbench.modeling.EModelService;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.impl.CommandsPackageImpl;
import org.eclipse.e4.ui.model.application.commands.provider.CommandsItemProviderAdapterFactory;
import org.eclipse.e4.ui.model.application.descriptor.basic.provider.BasicItemProviderAdapterFactory;
import org.eclipse.e4.ui.model.application.impl.ApplicationPackageImpl;
import org.eclipse.e4.ui.model.application.provider.ApplicationItemProviderAdapterFactory;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.impl.AdvancedPackageImpl;
import org.eclipse.e4.ui.model.application.ui.advanced.provider.AdvancedItemProviderAdapterFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.basic.impl.BasicPackageImpl;
import org.eclipse.e4.ui.model.application.ui.impl.UiPackageImpl;
import org.eclipse.e4.ui.model.application.ui.menu.impl.MenuPackageImpl;
import org.eclipse.e4.ui.model.application.ui.menu.provider.MenuItemProviderAdapterFactory;
import org.eclipse.e4.ui.model.application.ui.provider.ElementContainerItemProvider;
import org.eclipse.e4.ui.model.application.ui.provider.UiItemProviderAdapterFactory;
import org.eclipse.e4.xwt.emf.EMFHelper;
import org.eclipse.e4.xwt.tools.categorynode.node.CategoryNode;
import org.eclipse.e4.xwt.tools.categorynode.node.NodeFactory;
import org.eclipse.e4.xwt.tools.categorynode.node.provider.NodeItemProviderAdapterFactory;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.swt.graphics.Image;

/**
 * 
 * @author yyang <yves.yang@soyatec.com>
 */
public class ApplicationModelHelper {
	private static Object[] EMPTY = new Object[0];
	private static ComposedAdapterFactory adapterFactory;
	private static AdapterFactoryContentProvider contentProvider;
	private static AdapterFactoryLabelProvider labelProvider;
	private static List<EClass> modelClasses;
	private static Map<EClass, Map<String, EClass>> referenceTypeMap = new HashMap<EClass, Map<String, EClass>>();

	static {
		// Application
		{
			Map<String, EClass> classMap = new HashMap<String, EClass>();
			classMap.put(
					"commands",
					org.eclipse.e4.ui.model.application.commands.impl.CommandsPackageImpl.eINSTANCE
							.getCommand());
			classMap.put(
					"addons",
					org.eclipse.e4.ui.model.application.impl.ApplicationPackageImpl.eINSTANCE
							.getAddon());
			classMap.put(
					"children",
					org.eclipse.e4.ui.model.application.ui.basic.impl.BasicPackageImpl.eINSTANCE
							.getWindow());
			referenceTypeMap
					.put(
							org.eclipse.e4.ui.model.application.impl.ApplicationPackageImpl.eINSTANCE
									.getApplication(), classMap);
		}
		// Contribution
		{
			Map<String, EClass> classMap = new HashMap<String, EClass>();
			classMap.put(
					"persistedState",
					org.eclipse.e4.ui.model.application.impl.ApplicationPackageImpl.eINSTANCE
							.getStringToStringMap());
			referenceTypeMap
					.put(
							org.eclipse.e4.ui.model.application.impl.ApplicationPackageImpl.eINSTANCE
									.getContribution(), classMap);
		}
		// ModelComponents
		{
			Map<String, EClass> classMap = new HashMap<String, EClass>();
			classMap.put(
					"components",
					org.eclipse.e4.ui.model.application.impl.ApplicationPackageImpl.eINSTANCE
							.getModelComponent());
			referenceTypeMap
					.put(
							org.eclipse.e4.ui.model.application.impl.ApplicationPackageImpl.eINSTANCE
									.getModelComponents(), classMap);
		}
		// ModelComponent
		{
			Map<String, EClass> classMap = new HashMap<String, EClass>();
			classMap.put(
					"children",
					org.eclipse.e4.ui.model.application.ui.impl.UiPackageImpl.eINSTANCE
							.getUIElement());
			classMap.put(
					"commands",
					org.eclipse.e4.ui.model.application.commands.impl.CommandsPackageImpl.eINSTANCE
							.getCommand());
			classMap.put(
					"bindings",
					org.eclipse.e4.ui.model.application.commands.impl.CommandsPackageImpl.eINSTANCE
							.getKeyBinding());
			referenceTypeMap
					.put(
							org.eclipse.e4.ui.model.application.impl.ApplicationPackageImpl.eINSTANCE
									.getModelComponent(), classMap);
		}
		// BindingTableContainer
		{
			Map<String, EClass> classMap = new HashMap<String, EClass>();
			classMap.put(
					"bindingTables",
					org.eclipse.e4.ui.model.application.commands.impl.CommandsPackageImpl.eINSTANCE
							.getBindingTable());
			referenceTypeMap
					.put(
							org.eclipse.e4.ui.model.application.commands.impl.CommandsPackageImpl.eINSTANCE
									.getBindingTableContainer(), classMap);
		}
		// BindingContext
		{
			Map<String, EClass> classMap = new HashMap<String, EClass>();
			classMap.put(
					"children",
					org.eclipse.e4.ui.model.application.commands.impl.CommandsPackageImpl.eINSTANCE
							.getBindingContext());
			referenceTypeMap
					.put(
							org.eclipse.e4.ui.model.application.commands.impl.CommandsPackageImpl.eINSTANCE
									.getBindingContext(), classMap);
		}
		// BindingTable
		{
			Map<String, EClass> classMap = new HashMap<String, EClass>();
			classMap.put(
					"bindings",
					org.eclipse.e4.ui.model.application.commands.impl.CommandsPackageImpl.eINSTANCE
							.getKeyBinding());
			referenceTypeMap
					.put(
							org.eclipse.e4.ui.model.application.commands.impl.CommandsPackageImpl.eINSTANCE
									.getBindingTable(), classMap);
		}
		// Command
		{
			Map<String, EClass> classMap = new HashMap<String, EClass>();
			classMap.put(
					"parameters",
					org.eclipse.e4.ui.model.application.commands.impl.CommandsPackageImpl.eINSTANCE
							.getCommandParameter());
			referenceTypeMap
					.put(
							org.eclipse.e4.ui.model.application.commands.impl.CommandsPackageImpl.eINSTANCE
									.getCommand(), classMap);
		}
		// HandlerContainer
		{
			Map<String, EClass> classMap = new HashMap<String, EClass>();
			classMap.put(
					"handlers",
					org.eclipse.e4.ui.model.application.commands.impl.CommandsPackageImpl.eINSTANCE
							.getHandler());
			referenceTypeMap
					.put(
							org.eclipse.e4.ui.model.application.commands.impl.CommandsPackageImpl.eINSTANCE
									.getHandlerContainer(), classMap);
		}
		// KeyBinding
		{
			Map<String, EClass> classMap = new HashMap<String, EClass>();
			classMap.put(
					"parameters",
					org.eclipse.e4.ui.model.application.commands.impl.CommandsPackageImpl.eINSTANCE
							.getParameter());
			referenceTypeMap
					.put(
							org.eclipse.e4.ui.model.application.commands.impl.CommandsPackageImpl.eINSTANCE
									.getKeyBinding(), classMap);
		}
		// Context
		{
			Map<String, EClass> classMap = new HashMap<String, EClass>();
			classMap.put(
					"properties",
					org.eclipse.e4.ui.model.application.impl.ApplicationPackageImpl.eINSTANCE
							.getStringToStringMap());
			referenceTypeMap
					.put(
							org.eclipse.e4.ui.model.application.ui.impl.UiPackageImpl.eINSTANCE
									.getContext(), classMap);
		}
		// ElementContainer
		{
			Map<String, EClass> classMap = new HashMap<String, EClass>();
			classMap.put(
					"children",
					org.eclipse.e4.ui.model.application.ui.impl.UiPackageImpl.eINSTANCE
							.getUIElement());
			referenceTypeMap
					.put(
							org.eclipse.e4.ui.model.application.ui.impl.UiPackageImpl.eINSTANCE
									.getElementContainer(), classMap);
		}
		// HandledItem
		{
			Map<String, EClass> classMap = new HashMap<String, EClass>();
			classMap.put(
					"parameters",
					org.eclipse.e4.ui.model.application.commands.impl.CommandsPackageImpl.eINSTANCE
							.getParameter());
			referenceTypeMap
					.put(
							org.eclipse.e4.ui.model.application.ui.menu.impl.MenuPackageImpl.eINSTANCE
									.getHandledItem(), classMap);
		}
		// Menu
		{
			Map<String, EClass> classMap = new HashMap<String, EClass>();
			classMap.put(
					"children",
					org.eclipse.e4.ui.model.application.ui.menu.impl.MenuPackageImpl.eINSTANCE
							.getMenuElement());
			referenceTypeMap
					.put(
							org.eclipse.e4.ui.model.application.ui.menu.impl.MenuPackageImpl.eINSTANCE
									.getMenu(), classMap);
		}
		// ToolItem
		{
			Map<String, EClass> classMap = new HashMap<String, EClass>();
			classMap.put(
					"children",
					org.eclipse.e4.ui.model.application.ui.menu.impl.MenuPackageImpl.eINSTANCE
							.getMenuElement());
			referenceTypeMap
					.put(
							org.eclipse.e4.ui.model.application.ui.menu.impl.MenuPackageImpl.eINSTANCE
									.getToolItem(), classMap);
		}
		// ToolBar
		{
			Map<String, EClass> classMap = new HashMap<String, EClass>();
			classMap.put(
					"children",
					org.eclipse.e4.ui.model.application.ui.menu.impl.MenuPackageImpl.eINSTANCE
							.getToolBarElement());
			referenceTypeMap
					.put(
							org.eclipse.e4.ui.model.application.ui.menu.impl.MenuPackageImpl.eINSTANCE
									.getToolBar(), classMap);
		}
		// Part
		{
			Map<String, EClass> classMap = new HashMap<String, EClass>();
			classMap.put(
					"menus",
					org.eclipse.e4.ui.model.application.ui.menu.impl.MenuPackageImpl.eINSTANCE
							.getMenu());
			referenceTypeMap
					.put(
							org.eclipse.e4.ui.model.application.ui.basic.impl.BasicPackageImpl.eINSTANCE
									.getPart(), classMap);
		}
		// PartStack
		{
			Map<String, EClass> classMap = new HashMap<String, EClass>();
			classMap.put(
					"children",
					org.eclipse.e4.ui.model.application.ui.basic.impl.BasicPackageImpl.eINSTANCE
							.getStackElement());
			referenceTypeMap
					.put(
							org.eclipse.e4.ui.model.application.ui.basic.impl.BasicPackageImpl.eINSTANCE
									.getPartStack(), classMap);
		}
		// PartSashContainer
		{
			Map<String, EClass> classMap = new HashMap<String, EClass>();
			classMap.put(
					"children",
					org.eclipse.e4.ui.model.application.ui.basic.impl.BasicPackageImpl.eINSTANCE
							.getPartSashContainerElement());
			referenceTypeMap
					.put(
							org.eclipse.e4.ui.model.application.ui.basic.impl.BasicPackageImpl.eINSTANCE
									.getPartSashContainer(), classMap);
		}
		// Window
		{
			Map<String, EClass> classMap = new HashMap<String, EClass>();
			classMap.put(
					"windows",
					org.eclipse.e4.ui.model.application.ui.basic.impl.BasicPackageImpl.eINSTANCE
							.getWindow());
			classMap.put(
					"sharedElements",
					org.eclipse.e4.ui.model.application.ui.impl.UiPackageImpl.eINSTANCE
							.getUIElement());
			classMap.put(
					"children",
					org.eclipse.e4.ui.model.application.ui.basic.impl.BasicPackageImpl.eINSTANCE
							.getWindowElement());
			referenceTypeMap
					.put(
							org.eclipse.e4.ui.model.application.ui.basic.impl.BasicPackageImpl.eINSTANCE
									.getWindow(), classMap);
		}
		// TrimmedWindow
		{
			Map<String, EClass> classMap = new HashMap<String, EClass>();
			classMap.put(
					"trimBars",
					org.eclipse.e4.ui.model.application.ui.basic.impl.BasicPackageImpl.eINSTANCE
							.getTrimBar());
			referenceTypeMap
					.put(
							org.eclipse.e4.ui.model.application.ui.basic.impl.BasicPackageImpl.eINSTANCE
									.getTrimmedWindow(), classMap);
		}
		// TrimBar
		{
			Map<String, EClass> classMap = new HashMap<String, EClass>();
			classMap.put(
					"children",
					org.eclipse.e4.ui.model.application.ui.basic.impl.BasicPackageImpl.eINSTANCE
							.getTrimElement());
			referenceTypeMap
					.put(
							org.eclipse.e4.ui.model.application.ui.basic.impl.BasicPackageImpl.eINSTANCE
									.getTrimBar(), classMap);
		}
		// Perspective
		{
			Map<String, EClass> classMap = new HashMap<String, EClass>();
			classMap.put(
					"windows",
					org.eclipse.e4.ui.model.application.ui.basic.impl.BasicPackageImpl.eINSTANCE
							.getWindow());
			classMap.put(
					"children",
					org.eclipse.e4.ui.model.application.ui.basic.impl.BasicPackageImpl.eINSTANCE
							.getPartSashContainerElement());
			referenceTypeMap
					.put(
							org.eclipse.e4.ui.model.application.ui.advanced.impl.AdvancedPackageImpl.eINSTANCE
									.getPerspective(), classMap);
		}
		// PerspectiveStack
		{
			Map<String, EClass> classMap = new HashMap<String, EClass>();
			classMap.put(
					"children",
					org.eclipse.e4.ui.model.application.ui.advanced.impl.AdvancedPackageImpl.eINSTANCE
							.getPerspective());
			referenceTypeMap
					.put(
							org.eclipse.e4.ui.model.application.ui.advanced.impl.AdvancedPackageImpl.eINSTANCE
									.getPerspectiveStack(), classMap);
		}
		// PartDescriptor
		{
			Map<String, EClass> classMap = new HashMap<String, EClass>();
			classMap.put(
					"menus",
					org.eclipse.e4.ui.model.application.ui.menu.impl.MenuPackageImpl.eINSTANCE
							.getMenu());
			referenceTypeMap
					.put(
							org.eclipse.e4.ui.model.application.descriptor.basic.impl.BasicPackageImpl.eINSTANCE
									.getPartDescriptor(), classMap);
		}
		// PartDescriptorContainer
		{
			Map<String, EClass> classMap = new HashMap<String, EClass>();
			classMap.put(
					"descriptors",
					org.eclipse.e4.ui.model.application.descriptor.basic.impl.BasicPackageImpl.eINSTANCE
							.getPartDescriptor());
			referenceTypeMap
					.put(
							org.eclipse.e4.ui.model.application.descriptor.basic.impl.BasicPackageImpl.eINSTANCE
									.getPartDescriptorContainer(), classMap);
		}
	}

	public synchronized static ComposedAdapterFactory getFactory() {
		if (adapterFactory == null) {
			adapterFactory = new ComposedAdapterFactory(
					ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
			adapterFactory
					.addAdapterFactory(new org.eclipse.e4.ui.model.application.ui.basic.provider.BasicItemProviderAdapterFactory());
			adapterFactory
					.addAdapterFactory(new BasicItemProviderAdapterFactory());
			adapterFactory
					.addAdapterFactory(new ResourceItemProviderAdapterFactory());
			adapterFactory
					.addAdapterFactory(new CommandsItemProviderAdapterFactory());
			adapterFactory
					.addAdapterFactory(new UiItemProviderAdapterFactory());
			adapterFactory
					.addAdapterFactory(new MenuItemProviderAdapterFactory());
			adapterFactory
					.addAdapterFactory(new AdvancedItemProviderAdapterFactory());
			adapterFactory
					.addAdapterFactory(new ApplicationItemProviderAdapterFactory());
			adapterFactory
					.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
			adapterFactory
					.addAdapterFactory(new NodeItemProviderAdapterFactory());
		}
		return adapterFactory;
	}

	public synchronized static AdapterFactoryContentProvider getContentProvider() {
		if (contentProvider == null) {
			contentProvider = new AdapterFactoryContentProviderEx(getFactory());
		}
		return contentProvider;
	}

	public synchronized static List<EClass> getModelClasses() {
		if (modelClasses == null) {
			modelClasses = new ArrayList<EClass>();
		}

		Class<?>[] packageClasses = new Class[] {
				ApplicationPackageImpl.Literals.class,
				BasicPackageImpl.Literals.class, UiPackageImpl.Literals.class,
				CommandsPackageImpl.Literals.class,
				MenuPackageImpl.Literals.class,
				AdvancedPackageImpl.Literals.class };

		for (Class<?> packageClass : packageClasses) {
			Field[] fields = packageClass.getFields();
			for (int i = 0; i < fields.length; i++) {
				try {
					Object value = fields[i].get(null);
					if (value instanceof EClass) {
						EClass eClass = (EClass) value;
						if (!modelClasses.contains(eClass)) {
							modelClasses.add(eClass);
						}
					}
				} catch (Exception e) {
					continue;
				}
			}
		}
		return modelClasses;
	}

	public static List<EClass> getApplicationClasses() {
		return getImplementClasses(ApplicationPackageImpl.eINSTANCE
				.getApplicationElement());
	}

	public static List<EClass> getImplementClasses(EClass superType) {
		List<EClass> modelClasses = getModelClasses();
		List<EClass> implementClasses = new ArrayList<EClass>();
		for (EClass eClass : modelClasses) {
			if (implementClasses.contains(eClass)) {
				continue;
			}
			if (eClass.isAbstract() || eClass.isInterface()) {
				continue;
			}
			if (superType.isSuperTypeOf(eClass)) {
				implementClasses.add(eClass);
			}
		}
		return implementClasses;
	}

	public static CategoryNode[] getCategories(EObject eObj) {
		Collection<CategoryNode> children = new ArrayList<CategoryNode>();
		EStructuralFeature containmentFeature = eObj.eContainingFeature();
		if (eObj instanceof MApplication) {
			containmentFeature = UiPackageImpl.eINSTANCE
					.getElementContainer_Children();
		}
		EList<EReference> eReferences = eObj.eClass().getEAllReferences();
		for (EReference ref : eReferences) {
			if (!ref.isMany() || ref == containmentFeature) {
				continue;
			}
			CategoryNode node = NodeFactory.eINSTANCE.createCategoryNode();
			node.setObject(eObj);
			node.setReference(ref);
			children.add(node);
		}
		return children.toArray(new CategoryNode[0]);
	}

	static public Collection<?> getNodeChildren(EObject object) {
		Collection<Object> children = new ArrayList<Object>();
		EStructuralFeature containmentFeature = object.eContainingFeature();
		if (object instanceof MApplication) {
			containmentFeature = UiPackageImpl.eINSTANCE
					.getElementContainer_Children();
		}
		EList<EReference> eReferences = object.eClass().getEAllReferences();
		int elements = 0;
		EReference soloReference = null;
		for (EReference reference : eReferences) {
			if (reference.isMany()) {
				soloReference = reference;
				elements++;
			}
		}
		if (elements == 1) {
			children.addAll((Collection<?>) object.eGet(soloReference));
		} else {
			for (EReference reference : eReferences) {
				if (reference.isMany()) {
					if (containmentFeature != null
							&& reference == containmentFeature) {
						children.addAll((Collection<?>) object.eGet(reference));
					} else {
						CategoryNode node = NodeFactory.eINSTANCE
								.createCategoryNode();
						node.setObject((EObject) object);
						node.setReference(reference);
						children.add(node);
					}
				}
			}
		}
		return children;
	}

	public synchronized static AdapterFactoryLabelProvider getLabelProvider() {
		if (labelProvider == null) {
			labelProvider = new AdapterFactoryLabelProvider(getFactory());
		}
		return labelProvider;
	}

	public static Object[] getModelChildren(Object parent) {
		return getModelChildren(parent, new IFilter() {
			public boolean select(Object toTest) {
				if (toTest instanceof EObject) {
					return ((EObject) toTest).eResource() != null;
				}
				return false;
			}
		});
	}

	public static Object[] getModelChildren(Object parent, IFilter filter) {
		if (parent instanceof EObject) {
			Set<Object> result = new HashSet<Object>();
			EObject object = (EObject) parent;
			for (EObject child : object.eContents()) {
				if (filter == null || filter.select(child)) {
					result.add(child);
				}
			}
			return result.toArray(new Object[result.size()]);
		}
		return EMPTY;
	}

	public static Object[] getChildren(Object parent) {
		return getChildren(parent, new IFilter() {
			public boolean select(Object toTest) {
				if (toTest instanceof CategoryNode) {
					return true;
				}
				if (toTest instanceof EObject) {
					return ((EObject) toTest).eResource() != null;
				}
				return false;
			}
		}, false);
	}

	public static Object[] getChildren(Object parent, IFilter filter,
			boolean includeChildren) {
		Set<Object> result = new HashSet<Object>();
		Object[] children = getContentProvider().getChildren(parent);
		for (Object object : children) {
			if (includeChildren) {
				Object[] childList = getChildren(object, filter,
						includeChildren);
				result.addAll(Arrays.asList(childList));
			}
			if (filter == null || filter.select(object)) {
				result.add(object);
			}
		}
		return result.toArray(new Object[result.size()]);
	}

	public static Image getImage(Object object) {
		return getLabelProvider().getImage(object);
	}

	public static String getDisplayName(EObject object,
			EStructuralFeature feature) {
		if (object == null || feature == null) {
			return null;
		}
		ComposedAdapterFactory factory = getFactory();
		String name = feature.getName();
		IItemPropertySource ps = (IItemPropertySource) factory.adapt(object,
				IItemPropertySource.class);
		String displayName = null;
		if (ps != null) {
			IItemPropertyDescriptor pd = ps.getPropertyDescriptor(object, name);
			if (pd != null) {
				displayName = pd.getDisplayName(feature);
			}
		}
		return displayName;
	}

	public static String getText(Object object) {
		return getLabelProvider().getText(object);
	}

	public static boolean isLive(Object element) {
		if (!(element instanceof EObject)) {
			return false;
		}
		EObject eObject = (EObject) element;
		return (eObject != null && eObject.eResource() != null);
	}

	public static EReference findReference(EClass container, EClass target) {
		return findReference(container, target, new HashSet<String>());
	}

	protected static EReference findReference(EClass container, EClass target,
			Collection<String> checkedProperties) {
		Map<String, EClass> metaclassEntry = referenceTypeMap.get(container);
		if (metaclassEntry != null) {
			for (Map.Entry<String, EClass> entry : metaclassEntry.entrySet()) {
				String key = entry.getKey();
				if (checkedProperties.contains(key)) {
					continue;
				}
				checkedProperties.add(key);
				EClass value = entry.getValue();
				if (value.isSuperTypeOf(target) || value.equals(target)) {
					EReference reference = EMFHelper.findReference(container,
							key);
					if (reference != null) {
						return reference;
					}
				}
			}
		}

		for (EClass superType : container.getESuperTypes()) {
			EReference reference = findReference(superType, target,
					checkedProperties);
			if (reference != null) {
				return reference;
			}
		}
		return null;
	}

	public static Set<EClass> getAccessibleChildren(EClass type,
			boolean containsSupers) {
		Set<EClass> accessibles = new HashSet<EClass>();
		Map<String, EClass> metaclassEntry = referenceTypeMap.get(type);
		if (metaclassEntry != null) {
			Set<Entry<String, EClass>> entrySet = metaclassEntry.entrySet();
			for (Entry<String, EClass> entry : entrySet) {
				EClass value = entry.getValue();
				accessibles.add(value);
				accessibles.addAll(getImplementClasses(value));
			}
		}
		if (containsSupers) {
			EList<EClass> eSuperTypes = type.getESuperTypes();
			for (EClass superType : eSuperTypes) {
				accessibles.addAll(getAccessibleChildren(superType,
						containsSupers));
			}
		}
		return accessibles;
	}

	public static int getChildIndex(Object parent, Object child) {
		if (parent instanceof EObject) {
			EObject parentObject = (EObject) parent;
			EReference reference = ApplicationModelHelper.findReference(
					parentObject.eClass(), ((EObject) child).eClass());
			if (reference != null && reference.isMany()) {
				List listValue = (List) parentObject.eGet(reference);
				return listValue.indexOf(child);
			}
		}
		return -1;
	}

	public static boolean canAddedChild(Object target, Object element) {
		if (element instanceof EObject && target instanceof EObject) {
			return canAddedChild((EObject) target, (EObject) element);
		}
		return false;
	}

	private static boolean canAddedChild(EObject target, EObject element) {
		EClass elementType = (element instanceof EClass ? (EClass) element
				: element.eClass());
		EClass targetType = (target instanceof EClass ? (EClass) target
				: target.eClass());
		return findReference(targetType, elementType) != null;
	}

	static public MWindow findMWindow(MUIElement element) {
		MUIElement parent = element;
		while (parent != null && !(parent instanceof MWindow)) {
			parent = parent.getParent();
		}
		return (MWindow) parent;
	}

	static public EModelService findModelService(MUIElement element) {
		MWindow window = findMWindow(element);
		if (window == null) {
			return null;
		}
		IEclipseContext context = window.getContext();
		if (context == null) {
			return null;
		}
		Object value = context.get(EModelService.class.getName());
		if (value == null || !(value instanceof EModelService)) {
			return null;
		}
		return (EModelService) value;
	}

	public static Collection<? extends EStructuralFeature> getContainementFeatures(
			EObject object) {
		ITreeItemContentProvider treeItemContentProvider = (ITreeItemContentProvider) getFactory()
				.adapt(object, ITreeItemContentProvider.class);
		if (treeItemContentProvider != null
				&& treeItemContentProvider instanceof ElementContainerItemProvider) {
			return ((ElementContainerItemProvider) treeItemContentProvider)
					.getChildrenFeatures(object);
		}
		return null;
	}

	public static List<?> collectAllElements(EObject object, IFilter filter) {
		ArrayList<Object> collector = new ArrayList<Object>();
		Resource resource = object.eResource();
		if (resource != null) {
			ResourceSet resourceSet = object.eResource().getResourceSet();
			if (resourceSet != null) {
				for (TreeIterator<?> iterator = resourceSet.getAllContents(); iterator
						.hasNext();) {
					Object element = iterator.next();
					if (filter.select(element)) {
						collector.add(element);
					}
				}
			}
		}
		return collector;
	}

	public static List<?> collectAllElements(EObject object, EClassifier type) {
		ArrayList<Object> collector = new ArrayList<Object>();
		Resource resource = object.eResource();
		if (resource != null) {
			ResourceSet resourceSet = object.eResource().getResourceSet();
			if (resourceSet != null) {
				for (TreeIterator<?> iterator = resourceSet.getAllContents(); iterator
						.hasNext();) {
					Object element = iterator.next();
					if (type.isInstance(element)) {
						collector.add(element);
					}
				}
			}
		}
		return collector;
	}

	private static class AdapterFactoryContentProviderEx extends
			AdapterFactoryContentProvider {
		private static final Class<?> ITreeItemContentProviderClass = ITreeItemContentProvider.class;
		private static final EReference SF_CHILDREN = UiPackageImpl.eINSTANCE
				.getElementContainer_Children();

		public AdapterFactoryContentProviderEx(AdapterFactory adapterFactory) {
			super(adapterFactory);
		}

		public Object[] getChildren(Object object) {
			// Get the adapter from the factory.
			//
			ITreeItemContentProvider treeItemContentProvider = (ITreeItemContentProvider) adapterFactory
					.adapt(object, ITreeItemContentProviderClass);
			if (treeItemContentProvider == null) {
				return EMPTY;
			}
			if (object instanceof EObject
					&& !(object instanceof CategoryNode)
					&& treeItemContentProvider instanceof ElementContainerItemProvider) {
				Collection<? extends EStructuralFeature> childrenFeatures = ((ElementContainerItemProvider) treeItemContentProvider)
						.getChildrenFeatures(object);
				List<Object> children = new ArrayList<Object>();
				EObject eObj = (EObject) object;
				for (EStructuralFeature feature : childrenFeatures) {
					Object featureValue = eObj.eGet(feature);
					if (feature.isMany() && SF_CHILDREN == feature) {
						children.addAll((Collection<? extends Object>) featureValue);
					} else {
						CategoryNode node = NodeFactory.eINSTANCE
								.createCategoryNode();
						node.setObject(eObj);
						node.setReference((EReference) feature);
						children.add(node);
					}
				}
				return children.toArray();
			}
			// Either delegate the call or return nothing.
			//
			return (treeItemContentProvider != null ? treeItemContentProvider
					.getChildren(object) : Collections.EMPTY_LIST).toArray();
		}

	}
}
