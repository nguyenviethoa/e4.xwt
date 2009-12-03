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
package org.eclipse.e4.xwt.tools.ui.designer.editor.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.converters.StringToInteger;
import org.eclipse.e4.xwt.tools.ui.designer.commands.ApplyAttributeSettingCommand;
import org.eclipse.e4.xwt.tools.ui.designer.core.editor.EditDomain;
import org.eclipse.e4.xwt.tools.ui.designer.parts.WidgetEditPart;
import org.eclipse.e4.xwt.tools.ui.designer.swt.SWTStyles;
import org.eclipse.e4.xwt.tools.ui.designer.swt.StyleGroup;
import org.eclipse.e4.xwt.tools.ui.designer.utils.StringUtil;
import org.eclipse.e4.xwt.tools.ui.designer.utils.StyleHelper;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlAttribute;
import org.eclipse.e4.xwt.tools.ui.xaml.XamlNode;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Widget;

/**
 * @author jliu (jin.liu@soyatec.com)
 */
public class StyleAction extends Action implements IMenuCreator {

	public static final String ID = StyleAction.class.getName();
	private WidgetEditPart editPart;
	private MenuManager menuManager;

	public StyleAction(WidgetEditPart editPart) {
		this.editPart = editPart;
		setId(ID);
		setText("Style");
		setMenuCreator(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.e4.xwt.tools.ui.designer.editor.actions.AbstractDropDownAction#createMenuManager()
	 */
	protected MenuManager createMenuManager() {
		if (menuManager == null) {

			menuManager = new MenuManager();
			Widget widget = editPart.getWidget();
			if (widget != null && !widget.isDisposed()) {
				int masterStyle = widget.getStyle();
				StyleGroup[] styles = SWTStyles.getStyles(widget.getClass());
				Separator last = null;
				for (StyleGroup styleGroup : styles) {
					if (!styleGroup.match(masterStyle)) {
						continue;
					}
					String[] items = styleGroup.getStyles();
					String groupName = styleGroup.getGroupName();
					for (String style : items) {
						SetStyleAction action = new SetStyleAction(editPart.getCastModel(), styleGroup, style, "default".equals(groupName) ? AS_CHECK_BOX : AS_RADIO_BUTTON);
						boolean checked = StyleHelper.checkStyle(masterStyle, style);
						action.setChecked(checked);
						menuManager.add(action);
					}
					menuManager.add(last = new Separator(groupName));
				}
				if (last != null) {
					menuManager.remove(last);
				}
			}
		}
		return menuManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.IMenuCreator#dispose()
	 */
	public void dispose() {
		if (menuManager != null) {
			menuManager.dispose();
			menuManager = null;
		}
	}

	public Menu getMenu(Control parent) {
		MenuManager mm = createMenuManager();
		if (mm != null) {
			return mm.createContextMenu(parent);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Menu)
	 */
	public Menu getMenu(Menu parent) {
		MenuManager mm = createMenuManager();
		if (mm != null) {
			Menu menu = new Menu(parent);
			IContributionItem[] items = mm.getItems();
			for (int i = 0; i < items.length; i++) {
				IContributionItem item = items[i];
				IContributionItem newItem = item;
				if (item instanceof ActionContributionItem) {
					newItem = new ActionContributionItem(((ActionContributionItem) item).getAction());
				}
				newItem.fill(menu, -1);
			}
			return menu;
		}
		return null;
	}

	private class SetStyleAction extends Action {
		private String newStyle;
		private XamlNode node;
		private StyleGroup group;

		public SetStyleAction(XamlNode node, StyleGroup group, String newStyle, int actionStyle) {
			super(newStyle, actionStyle);
			this.node = node;
			this.group = group;
			this.newStyle = newStyle;
		}

		void execute(String newStyle) {
			EditDomain.getEditDomain(editPart).getCommandStack().execute(new ApplyAttributeSettingCommand(node, "style", IConstants.XWT_X_NAMESPACE, newStyle));
		}

		public void run() {
			// fail fast
			if (getStyle() == AS_RADIO_BUTTON && !isChecked()) {
				return;
			}

			XamlAttribute attribute = node.getAttribute("style", IConstants.XWT_X_NAMESPACE);
			if (attribute == null || attribute.getValue() == null) {
				execute(newStyle);
			} else {
				String value = attribute.getValue();
				List<String> oldValues = new ArrayList<String>();
				StringTokenizer stk = new StringTokenizer(value, "|");
				while (stk.hasMoreTokens()) {
					oldValues.add(stk.nextToken().trim());
				}

				// 1. Maybe not a string.
				if (oldValues.isEmpty()) {
					int style = StyleHelper.getStyle(node);
					int newStyleValue = (Integer) StringToInteger.instance.convert(newStyle);
					execute(Integer.toString(style | newStyleValue));
					return;
				}
				// 2. String style.
				if (getStyle() == AS_RADIO_BUTTON) {
					String[] styles = group.getStyles();
					for (String str : styles) {
						if (oldValues.contains(str)) {
							oldValues.remove(str);
						}
						if (oldValues.contains("SWT." + str)) {
							oldValues.remove("SWT." + str);
						}
					}
					oldValues.add(newStyle);
					String newStyleValue = StringUtil.format(oldValues.toArray(new String[oldValues.size()]), "|");
					execute(newStyleValue);
				} else {
					if (isChecked()) {
						// new add.
						if (oldValues.contains(newStyle) || oldValues.contains("SWT." + newStyle)) {
							return;
						}
						String styleValue = value + "|" + newStyle;
						execute(styleValue);
					} else {
						// remove
						if (oldValues.contains(newStyle)) {
							oldValues.remove(newStyle);
						}
						if (oldValues.contains("SWT." + newStyle)) {
							oldValues.remove("SWT." + newStyle);
						}
						String newStyleValue = StringUtil.format(oldValues.toArray(new String[oldValues.size()]), "|");
						execute(newStyleValue);
					}
				}
			}
		}
	}

}
