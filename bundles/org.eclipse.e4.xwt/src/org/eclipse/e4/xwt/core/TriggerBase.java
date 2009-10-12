/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.core;

import java.util.HashMap;

import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.internal.utils.LoggerManager;
import org.eclipse.e4.xwt.internal.utils.UserDataHelper;
import org.eclipse.swt.widgets.Widget;

public abstract class TriggerBase {
	public final static TriggerBase[] EMPTY_ARRAY = new TriggerBase[0];
	protected TriggerAction[] entryActions;
	protected TriggerAction[] exitActions;

	public TriggerAction[] getEntryActions() {
		if (entryActions == null) {
			return TriggerAction.EMPTY_ARRAY;
		}
		return entryActions;
	}

	public void setEntryActions(TriggerAction[] entryActions) {
		this.entryActions = entryActions;
	}

	public TriggerAction[] getExitActions() {
		if (exitActions == null) {
			return TriggerAction.EMPTY_ARRAY;
		}
		return exitActions;
	}

	public void setExitActions(TriggerAction[] exitActions) {
		this.exitActions = exitActions;
	}
	
	public abstract void on(Object target);
	
	abstract class AbstractValueChangeListener implements IValueChangeListener {
		protected HashMap<SetterBase, Object> oldvalues = null;
		protected Object element;

		public AbstractValueChangeListener(Object element) {
			this.element = element;
		}
		
		protected void restoreValues() {
			if (oldvalues == null) {
				return;
			}
			for (SetterBase setter : oldvalues.keySet()) {
				setter.undo(element, oldvalues.get(setter));
			}
		}
	}
	
	public static Object getElementByName(Object target, String elementName) {
		if (elementName != null && elementName.length() > 0) {
			Widget widget = UserDataHelper.getWidget(target);
			if (widget != null) {
				Object element = XWT.findElementByName(widget, elementName);
				if (element != null) {
					return element;
				}
				LoggerManager.log("EventTrigger: Source \n" + elementName + "\n is not found in " + target.getClass().getName());
			}
		}
		return target;
	}

}
