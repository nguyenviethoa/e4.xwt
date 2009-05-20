package org.eclipse.e4.xwt;

import org.eclipse.e4.xwt.databinding.BindingMode;

public interface IDataBinding {
	public IDataProvider getDataProvider();

	BindingMode getBindingMode();

	IValueConverter getConverter();

	public Object getValue();

	public Object getTarget();
}
