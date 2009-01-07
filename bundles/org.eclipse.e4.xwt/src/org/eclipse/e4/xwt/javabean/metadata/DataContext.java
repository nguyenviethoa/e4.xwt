package org.eclipse.e4.xwt.javabean.metadata;

import java.lang.reflect.Method;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.javabean.ResourceLoader;
import org.eclipse.e4.xwt.javabean.StaticResourceBinding;
import org.eclipse.e4.xwt.javabean.metadata.DynamicProperty;
import org.eclipse.e4.xwt.utils.LoggerManager;
import org.eclipse.e4.xwt.xml.Attribute;
import org.eclipse.e4.xwt.xml.DocumentObject;
import org.eclipse.e4.xwt.xml.Element;
import org.eclipse.swt.widgets.Widget;

public class DataContext extends DynamicProperty {

	public DataContext(String name) {
		super(null, null, null, name);
	}

	public Object getValue(Object target) {
		Widget widget = (Widget) target;
		return widget.getData(IConstants.XWT_DATACONTEXT_KEY);
	}

	public void setValue(Object target, Object value) {
		Widget widget = (Widget) target;
		widget.setData(IConstants.XWT_DATACONTEXT_KEY, value);
	}
}
