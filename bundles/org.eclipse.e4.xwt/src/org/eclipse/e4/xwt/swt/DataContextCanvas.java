package org.eclipse.e4.xwt.swt;

import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.impl.IDataContextControl;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class DataContextCanvas extends Canvas implements IDataContextControl {

	public DataContextCanvas(Composite parent, int style) {
		super(parent, style);
	}

	public Object getDataContext() {
		return XWT.getDataContext(this);
	}

	public void setDataContext(Object object) {
		XWT.setDataContext(this, object);
	}
}
