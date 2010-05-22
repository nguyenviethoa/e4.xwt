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
package org.eclipse.e4.tools.ui.dataform;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.e4.tools.ui.designer.E4DesignerPlugin;
import org.eclipse.e4.ui.model.application.commands.impl.CommandsPackageImpl;
import org.eclipse.e4.ui.model.application.descriptor.basic.impl.BasicPackageImpl;
import org.eclipse.e4.ui.model.application.impl.ApplicationPackageImpl;
import org.eclipse.e4.ui.model.application.ui.advanced.impl.AdvancedPackageImpl;
import org.eclipse.e4.ui.model.application.ui.impl.UiPackageImpl;
import org.eclipse.e4.ui.model.application.ui.menu.impl.MenuPackageImpl;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.XWTLoader;
import org.eclipse.e4.xwt.databinding.BindingContext;
import org.eclipse.e4.xwt.emf.EMFBinding;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class DataForms {

	private static final String JAVA_PREFIX = "org.eclipse.e4.tools.ui.dataform.workbench";
	private static final String SUFFIX = "DataForm";

	private static List<EStructuralFeature> REQUIRED_SF_LIST;
	private static List<EAttribute> URI_ATTR_LIST;
	private static List<EAttribute> REF_ATTR_LIST;

	private static Map<Composite, Map<URL, AbstractDataForm>> widgetsCache = new HashMap<Composite, Map<URL, AbstractDataForm>>(
			1);

	public synchronized static List<EStructuralFeature> getRequiredFeatures() {
		if (REQUIRED_SF_LIST == null) {
			REQUIRED_SF_LIST = new ArrayList<EStructuralFeature>();
			REQUIRED_SF_LIST
					.add(ApplicationPackageImpl.Literals.CONTRIBUTION__CONTRIBUTION_URI);
			// REQUIRED_SF_LIST
			// .add(ApplicationPackageImpl.Literals.APPLICATION_ELEMENT__ELEMENT_ID);
			REQUIRED_SF_LIST
					.add(ApplicationPackageImpl.Literals.STRING_TO_STRING_MAP__KEY);

			REQUIRED_SF_LIST
					.add(CommandsPackageImpl.Literals.BINDING_CONTEXT__NAME);
			REQUIRED_SF_LIST
					.add(CommandsPackageImpl.Literals.BINDING_TABLE__BINDING_CONTEXT_ID);
			REQUIRED_SF_LIST
					.add(CommandsPackageImpl.Literals.COMMAND__COMMAND_NAME);
			REQUIRED_SF_LIST.add(CommandsPackageImpl.Literals.HANDLER__COMMAND);
			REQUIRED_SF_LIST
					.add(CommandsPackageImpl.Literals.KEY_BINDING__COMMAND);

			REQUIRED_SF_LIST
					.add(BasicPackageImpl.Literals.PART_DESCRIPTOR__CONTRIBUTION_URI);

			REQUIRED_SF_LIST.add(UiPackageImpl.Literals.INPUT__INPUT_URI);
			REQUIRED_SF_LIST.add(UiPackageImpl.Literals.UI_LABEL__LABEL);

			REQUIRED_SF_LIST.add(AdvancedPackageImpl.Literals.PLACEHOLDER__REF);

			REQUIRED_SF_LIST
					.add(MenuPackageImpl.Literals.HANDLED_ITEM__COMMAND);
			REQUIRED_SF_LIST.add(MenuPackageImpl.Literals.ITEM__TYPE);
		}
		return REQUIRED_SF_LIST;
	}

	public synchronized static List<EAttribute> getURIAttributes() {
		if (URI_ATTR_LIST == null) {
			URI_ATTR_LIST = new ArrayList<EAttribute>();
			URI_ATTR_LIST
					.add(ApplicationPackageImpl.Literals.CONTRIBUTION__CONTRIBUTION_URI);
			URI_ATTR_LIST
					.add(BasicPackageImpl.Literals.PART_DESCRIPTOR__CONTRIBUTION_URI);
			URI_ATTR_LIST.add(UiPackageImpl.Literals.INPUT__INPUT_URI);
			URI_ATTR_LIST.add(UiPackageImpl.Literals.UI_LABEL__ICON_URI);
		}
		return URI_ATTR_LIST;
	}

	public synchronized static List<EAttribute> getRefAttributes() {
		if (REF_ATTR_LIST == null) {
			REF_ATTR_LIST = new ArrayList<EAttribute>();
			REF_ATTR_LIST
					.add(CommandsPackageImpl.Literals.BINDING_TABLE__BINDING_CONTEXT_ID);
			REF_ATTR_LIST
					.add(CommandsPackageImpl.Literals.COMMAND_PARAMETER__TYPE_ID);
			REF_ATTR_LIST.add(UiPackageImpl.Literals.CONTEXT__CONTEXT);
			REF_ATTR_LIST
					.add(MenuPackageImpl.Literals.HANDLED_ITEM__WB_COMMAND);
		}
		return REF_ATTR_LIST;
	}

	public static boolean isRequiredSF(EStructuralFeature feature) {
		return feature != null && getRequiredFeatures().contains(feature);
	}

	public static boolean isURI_SF(EStructuralFeature feature) {
		return feature != null && feature instanceof EAttribute
				&& getURIAttributes().contains(feature);
	}

	public static boolean isRefSF(EStructuralFeature feature) {
		return feature != null && feature instanceof EAttribute
				&& getRefAttributes().contains(feature);
	}

	public static Class<?> findWidgetCLR(EClass eClass)
			throws ClassNotFoundException {
		if (eClass == null) {
			return null;
		}
		String name = eClass.getName();
		return DataForms.class.getClassLoader().loadClass(
				JAVA_PREFIX + "." + name + SUFFIX);
	}

	public static URL findWidget(EClass eClass) {
		Class<?> clr;
		try {
			clr = findWidgetCLR(eClass);
		} catch (ClassNotFoundException e) {
			return null;
		}
		return clr.getResource(clr.getSimpleName()
					+ IConstants.XWT_EXTENSION_SUFFIX);
	}

	public static URL findWidget(Class<?> clr) {
		return clr.getResource(clr.getSimpleName()
					+ IConstants.XWT_EXTENSION_SUFFIX);
	}

	public static AbstractDataForm getWidget(Composite parent, EClass eType) {
		if (parent == null || parent.isDisposed() || eType == null) {
			return null;
		}
		Class<?> clr;
		try {
			clr = findWidgetCLR(eType);
		} catch (ClassNotFoundException e) {
			E4DesignerPlugin.logError(e);
			return null;
		}
		URL url = findWidget(clr);
		if (url == null) {
			E4DesignerPlugin.logError("Data Form for " + eType.getName() + " is not found.");
			return null;
		}
		Map<URL, AbstractDataForm> widgets = widgetsCache.get(parent);
		if (widgets == null) {
			widgetsCache.put(parent,
					widgets = new HashMap<URL, AbstractDataForm>());
		}
		AbstractDataForm w = widgets.get(url);
		if (w == null || w.isDisposed()) {
			w = createWidget(eType, parent, new HashMap<String, Object>());
			widgets.put(url, w);
		}
		return w;
	}

	public static AbstractDataForm createWidget(EClass eType, Composite parent,
			Map<String, Object> options) {
		if (eType == null || parent == null || parent.isDisposed()) {
			return null;
		}
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		parent.setVisible(false);
		try {
			EMFBinding.initialze();
			Class<?> clr = findWidgetCLR(eType);
			if (clr == null) {
				E4DesignerPlugin.logError("Data Form controller for " + eType.getName() + " is not fund");
				return null;
			}
			Thread.currentThread().setContextClassLoader(clr.getClassLoader());
			URL url = findWidget(clr);
			if (url == null) {
				E4DesignerPlugin.logError("Data Form controller for " + eType.getName() + " is not fund");
				return null;
			}
			BindingContext bindingContext = new BindingContext(parent);
			if (options == null) {
				options = new HashMap<String, Object>();
			}
			options.put(XWTLoader.CONTAINER_PROPERTY, parent);
			options.put(XWTLoader.DATACONTEXT_PROPERTY, EcoreUtil.create(eType));
			options.put(XWTLoader.BINDING_CONTEXT_PROPERTY, bindingContext);
			options.put(XWTLoader.CLASS_PROPERTY, clr);

			AbstractDataForm dataformControl = (AbstractDataForm) XWT
					.loadWithOptions(url, options);
			Layout layout = parent.getLayout();
			if (layout == null || layout instanceof GridLayout) {
				GridLayoutFactory.swtDefaults().generateLayout(parent);
			}
			parent.layout(true, true);
			dataformControl.setBindingContext(bindingContext);
			return dataformControl;
		} catch (Exception e) {
			E4DesignerPlugin.logError(e);
		} finally {
			Thread.currentThread().setContextClassLoader(classLoader);
			parent.setVisible(true);
		}
		return null;
	}
}
