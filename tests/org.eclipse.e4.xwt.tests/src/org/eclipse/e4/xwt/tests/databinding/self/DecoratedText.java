package org.eclipse.e4.xwt.tests.databinding.self;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.swt.widgets.Composite;

public class DecoratedText extends Composite {
	protected String decorator;
	
	public DecoratedText(Composite parent, int style) {
		super(parent, style);
	}

	public String getDecorator() {
		return decorator;
	}

	public void setDecorator(String decorator) {
		this.decorator = decorator;
	}
	
	public static void main(String[] args) {

		URL url = DecoratedText.class.getResource(DecoratedText.class
				.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX);
		try {
			XWT.open(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
