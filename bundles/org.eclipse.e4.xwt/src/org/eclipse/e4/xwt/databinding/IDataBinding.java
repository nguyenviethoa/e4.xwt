package org.eclipse.e4.xwt.databinding;

import org.eclipse.e4.xwt.IDataProvider;
import org.eclipse.e4.xwt.IValueConverter;

public interface IDataBinding {
	public IDataProvider getDataProvider();

	BindingMode getBindingMode();

	IValueConverter getConverter();

	public Object getValue();

	public Object getTarget();
}
