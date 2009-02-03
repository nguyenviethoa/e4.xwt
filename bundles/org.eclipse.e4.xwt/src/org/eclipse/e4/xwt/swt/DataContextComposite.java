package org.eclipse.e4.xwt.swt;

import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.impl.IDataContextControl;
import org.eclipse.swt.widgets.Composite;

public class DataContextComposite extends Composite implements IDataContextControl {

	public DataContextComposite(Composite parent, int style) {
		super(parent, style);
	}

	public Object getDataContext() {
		return XWT.getDataContext(this);
	}

	public void setDataContext(Object object) {
		XWT.setDataContext(this, object);
	}
}
