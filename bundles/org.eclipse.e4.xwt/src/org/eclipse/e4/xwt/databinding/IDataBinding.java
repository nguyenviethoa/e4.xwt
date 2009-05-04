package org.eclipse.e4.xwt.databinding;

import org.eclipse.e4.xwt.dataproviders.IDataProvider;

public interface IDataBinding {
	public IDataProvider getDataProvider();

	public Object getValue();
	
	public Object getTarget();
}
