package org.eclipse.e4.xwt;

import org.eclipse.e4.xwt.databinding.BindingMode;

public interface IDataBindingInfo {
	public IDataProvider getDataProvider();

	BindingMode getMode();

	IValueConverter getConverter();
}
