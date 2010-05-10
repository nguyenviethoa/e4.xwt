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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.provider.CommandsItemProviderAdapterFactory;
import org.eclipse.e4.ui.model.application.descriptor.basic.provider.BasicItemProviderAdapterFactory;
import org.eclipse.e4.ui.model.application.node.CategoryNode;
import org.eclipse.e4.ui.model.application.node.NodeFactory;
import org.eclipse.e4.ui.model.application.node.provider.NodeItemProviderAdapterFactory;
import org.eclipse.e4.ui.model.application.provider.ApplicationItemProviderAdapterFactory;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.advanced.impl.AdvancedPackageImpl;
import org.eclipse.e4.ui.model.application.ui.advanced.provider.AdvancedItemProviderAdapterFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainer;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.impl.UiPackageImpl;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.model.application.ui.menu.impl.MenuPackageImpl;
import org.eclipse.e4.ui.model.application.ui.menu.provider.MenuItemProviderAdapterFactory;
import org.eclipse.e4.ui.model.application.ui.provider.ElementContainerItemProvider;
import org.eclipse.e4.ui.model.application.ui.provider.UiItemProviderAdapterFactory;
import org.eclipse.e4.workbench.modeling.EModelService;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
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

	public static boolean canAddedChild(EClass eClass, MUIElement target) {
		// EClass eClass = (EClass) entry.getType();
		EClass toolBarClass = MenuPackageImpl.eINSTANCE.getToolBar();
		EClass menuClass = MenuPackageImpl.eINSTANCE.getMenu();
		if (target instanceof MPart
				&& !((toolBarClass.isSuperTypeOf(eClass) || toolBarClass == eClass) || (menuClass
						.isSuperTypeOf(eClass) || menuClass == eClass))) {
			return false;
		}

		if ((eClass == menuClass) && (target instanceof MMenu)) {
			return false;
		}

		if ((eClass == toolBarClass) && (target instanceof MToolBar)) {
			return false;
		}

		if ((eClass == menuClass)
				&& (!(target instanceof MWindow || target instanceof MPart))) {
			return false;
		}

		// accept only MMenuItem by MMenu
		if ((eClass == MenuPackageImpl.eINSTANCE.getMenuItem())
				&& !(target instanceof MMenu)) {
			return false;
		}

		// accept only MToolItem by MToolBar
		if ((eClass == MenuPackageImpl.eINSTANCE.getToolItem())
				&& !(target instanceof MToolBar)) {
			return false;
		}

		// accept only MPerspective by MPerspectiveStack
		if (eClass == AdvancedPackageImpl.eINSTANCE.getPerspective()
				&& !(target instanceof MPerspectiveStack)) {
			return false;
		}
		return true;
	}

	public static boolean canAddedChild(MUIElement element, MUIElement target) {
		if (element == null || target == null) {
			return false;
		}
		boolean canAdd = determineByGeneric(element.getClass(), target
				.getClass());
		if (!canAdd) {
			return false;
		}
		if (target instanceof MPartSashContainer) {
			canAdd = element instanceof MPartSashContainer
					|| element instanceof MPartStack;
		} else if (target instanceof MPartStack) {
			canAdd = element instanceof MPart;
		}
		return canAdd;
	}

	static private boolean determineByGeneric(Class<?> childType,
			Class<?> targetType) {
		if (childType == null || targetType == null) {
			return false;
		}
		Class<?> genericType = getGenericType(targetType);
		return genericType == null || genericType.isAssignableFrom(childType);
	}

	static public Class<?> getGenericType(Type type) {
		if (type == null) {
			return null;
		}
		Class<?> genericType = null;
		if ((type instanceof ParameterizedType)) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Type[] actualTypeArguments = parameterizedType
					.getActualTypeArguments();
			for (Type actualType : actualTypeArguments) {
				if (actualType instanceof Class<?>) {
					genericType = (Class<?>) actualType;
					break;
				}
			}
		} else if (type instanceof Class<?> && EObject.class != type
				&& Object.class != type) {
			Class<?> classType = (Class<?>) type;
			Type genericSuperclass = classType.getGenericSuperclass();
			genericType = getGenericType(genericSuperclass);
			if (genericType == null) {
				Type[] genericInterfaces = classType.getGenericInterfaces();
				for (Type generic : genericInterfaces) {
					genericType = getGenericType(generic);
					if (genericType != null) {
						break;
					}
				}
			}
		}
		return genericType;
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
