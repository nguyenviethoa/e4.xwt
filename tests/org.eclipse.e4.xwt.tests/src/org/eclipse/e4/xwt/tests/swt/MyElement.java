package org.eclipse.e4.xwt.tests.swt;

import org.eclipse.swt.widgets.Composite;

public class MyElement extends Composite {
	public static final int MY_STYLE = 0xF0000000;
	
	protected boolean myStyle = false; 
	
	public MyElement(Composite parent, int style) {
		super(parent, style);
		
		if ((style & MY_STYLE) == MY_STYLE) {
			myStyle = true;
		}
	}

	public boolean isMyStyle() {
		return myStyle;
	}

	public void setMyStyle(boolean myStyle) {
		this.myStyle = myStyle;
	}

}
