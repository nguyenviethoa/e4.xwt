package org.eclipse.e4.xwt.databinding;

import java.lang.reflect.Method;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.XWTException;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public class ObservableValueUtil {
	static final String TEXT = "text";
	public static final Class<?>[] ARGUMENT_TYPES = new Class[] {Control.class};

	public static IObservableValue observePropertyValue(Control control, String property) {
		if (TEXT.equalsIgnoreCase(property)) {
			if (control instanceof Text)
				return SWTObservables.observeText(control, SWT.Modify);
			return SWTObservables.observeText(control);
		} else {
			String getterName = "observe" + property.substring(0, 1).toUpperCase() + property.substring(1);
			Method method;
			try {
				method = SWTObservables.class.getMethod(getterName, ARGUMENT_TYPES);
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
}
