package org.eclipse.e4.xwt.javabean.metadata.properties;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWTException;
import org.eclipse.e4.xwt.impl.Style;

public class StyleProperty extends AbstractProperty {

	public StyleProperty() {
		super(IConstants.XAML_STYLE, Object.class);
	}

	public Object getValue(Object target) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchFieldException {
		return null;
	}

	public void setValue(Object target, Object value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchFieldException {
		if (!(value instanceof Style)) {
			throw new XWTException("Style is expected.");
		}
		Style style = (Style) value;
		style.apply(target);
	}
}
