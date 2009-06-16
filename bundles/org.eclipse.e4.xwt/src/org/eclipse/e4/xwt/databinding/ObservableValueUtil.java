package org.eclipse.e4.xwt.databinding;

import java.lang.reflect.Method;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.XWTException;
import org.eclipse.e4.xwt.internal.databinding.menuitem.MenuItemEnabledObservableValue;
import org.eclipse.e4.xwt.internal.databinding.menuitem.MenuItemSelectionObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public class ObservableValueUtil {
	static final String ENABLED = "enabled";
	static final String SELECTION = "selection";

	static final String TEXT = "text";
	public static final Class<?>[] CONTROL_ARGUMENT_TYPES = new Class[] { Control.class };
	public static final Class<?>[] VIEWER_ARGUMENT_TYPES = new Class[] { Viewer.class };

	public static IObservableValue createWidget(Object object, String property) {
		if (object instanceof Control) {
			try {
				return observePropertyValue((Control) object, property);
			} catch (XWTException e) {
			}
		}
		if (object instanceof MenuItem) {
			//
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=280157
			// testcase: org.eclipse.e4.xwt.tests.databinding.bindcontrol.BindMenuItem
			//
			if (ENABLED.equalsIgnoreCase(property)) {
				return new MenuItemEnabledObservableValue((MenuItem) object);
			} else if (SELECTION.equalsIgnoreCase(property)) {
				return new MenuItemSelectionObservableValue((MenuItem) object);
			}
		}
		if (object instanceof Viewer) {
			try {
				return observePropertyValue((Viewer) object, property);
			} catch (XWTException e) {
			}
		}
		return null;
	}

	public static IObservableValue observePropertyValue(Control control, String property) {
		if (TEXT.equalsIgnoreCase(property)) {
			if (control instanceof Text)
				return SWTObservables.observeText(control, SWT.Modify);
			// widget button is not supported at 3.4 version.
			if (SWT.getVersion() == 3449 && control instanceof Button) {
				return null;
			}
			try {
				return SWTObservables.observeText(control);
			} catch (IllegalArgumentException e) {
				throw new XWTException(e);
			}
		} else {
			if (property == null) {
				return null;
			}
			String getterName = "observe" + property.substring(0, 1).toUpperCase() + property.substring(1);
			Method method;
			try {
				method = SWTObservables.class.getMethod(getterName, CONTROL_ARGUMENT_TYPES);
				if (method == null) {
					for (Method element : SWTObservables.class.getMethods()) {
						if (element.getParameterTypes().length != 0) {
							continue;
						}
						if (element.getName().equalsIgnoreCase(getterName)) {
							method = element;
							break;
						}
					}
				}
				if (method != null) {
					return (IObservableValue) method.invoke(null, control);
				}
			} catch (Exception e) {
				throw new XWTException(e);
			}
		}
		return null;
	}

	public static IObservableValue observePropertyValue(Viewer viewer, String property) {
		if (property == null) {
			return null;
		}
		String getterName = "observe" + property.substring(0, 1).toUpperCase() + property.substring(1);
		Method method;
		try {
			method = ViewersObservables.class.getMethod(getterName, VIEWER_ARGUMENT_TYPES);
			if (method == null) {
				for (Method element : ViewersObservables.class.getMethods()) {
					if (element.getParameterTypes().length != 0) {
						continue;
					}
					if (element.getName().equalsIgnoreCase(getterName)) {
						method = element;
						break;
					}
				}
			}
			if (method != null) {
				return (IObservableValue) method.invoke(null, viewer);
			}
		} catch (Exception e) {
			throw new XWTException(e);
		}
		return null;
	}
}
