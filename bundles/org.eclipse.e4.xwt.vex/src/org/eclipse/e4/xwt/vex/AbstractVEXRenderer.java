package org.eclipse.e4.xwt.vex;

import org.eclipse.swt.widgets.Composite;

public abstract class AbstractVEXRenderer implements VEXRenderer {

	private Composite composite;

	public Composite getContainer() {
		return composite;
	}

	public void setContainer(Composite container) {
		this.composite = container;
	}

}
