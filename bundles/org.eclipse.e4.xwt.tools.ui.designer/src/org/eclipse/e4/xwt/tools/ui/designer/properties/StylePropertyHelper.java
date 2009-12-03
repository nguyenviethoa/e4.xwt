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
package org.eclipse.e4.xwt.tools.ui.designer.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.e4.xwt.converters.StringToInteger;
import org.eclipse.e4.xwt.jface.ComboBoxCellEditor;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.EditDomain;
import org.eclipse.e4.xwt.tools.ui.designer.properties.editors.BooleanCellEditor;
import org.eclipse.e4.xwt.tools.ui.designer.swt.SWTStyles;
import org.eclipse.e4.xwt.tools.ui.designer.swt.StyleGroup;
import org.eclipse.e4.xwt.tools.ui.designer.utils.StyleHelper;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class StylePropertyHelper {

	private static final String CATEGORY = "Style";
	private static final String PREFIX_STAR = "*";
	private Widget widget;
	private EditDomain editDomain;

	public StylePropertyHelper(Widget widget) {
		this.widget = widget;
	}

	public List<IPropertyDescriptor> createPropertyDescriptors() {
		if (widget == null || widget.isDisposed()) {
			return Collections.emptyList();
		}
		List<IPropertyDescriptor> descriptors = new ArrayList<IPropertyDescriptor>();
		StyleGroup[] styleGrps = SWTStyles.getStyles(widget.getClass());
		for (StyleGroup styleGroup : styleGrps) {
			String groupName = styleGroup.getGroupName();
			String[] styles = styleGroup.getStyles();
			if ("default".equals(groupName)) {
				for (String style : styles) {
					descriptors.add(createDescriptor(style));
				}
			} else {
				descriptors.add(createDescriptor(styleGroup));
			}
		}
		return descriptors;
	}

	private IPropertyDescriptor createDescriptor(final StyleGroup styleGroup) {
		PropertyDescriptor descriptor = new PropertyDescriptor(styleGroup, styleGroup.getGroupName()) {
			public CellEditor createPropertyEditor(Composite parent) {
				return new ComboBoxCellEditor(parent, styleGroup.getStyles());
			}
		};
		descriptor.setCategory(CATEGORY);
		return descriptor;
	}

	private IPropertyDescriptor createDescriptor(String style) {
		PropertyDescriptor descriptor = new PropertyDescriptor(PREFIX_STAR + style, style.toLowerCase()) {
			public CellEditor createPropertyEditor(Composite parent) {
				return new BooleanCellEditor(parent);
			}
		};
		descriptor.setCategory(CATEGORY);
		return descriptor;
	}

	public Object getPropertyValue(Object id) {
		if (id == null) {
			return null;
		} else if (id.toString().startsWith(PREFIX_STAR)) {
			String style = id.toString().substring(1);
			int intStyle = (Integer) StringToInteger.instance.convert(style);
			if (StyleHelper.checkStyle(widget, intStyle)) {
				return "true";
			}
			return "false";
		} else if (id instanceof StyleGroup) {
			StyleGroup styles = (StyleGroup) id;
			for (String style : styles.getStyles()) {
				int intStyle = (Integer) StringToInteger.instance.convert(style);
				if (StyleHelper.checkStyle(widget, intStyle)) {
					return style;
				}
			}
		}
		return null;
	}

	public void setPropertyValue(Object id, Object value) {

	}

	public void setEditDomain(EditDomain editDomain) {
		this.editDomain = editDomain;
	}

}
