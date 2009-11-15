package org.eclipse.e4.xwt.pde.ui;

import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.xwt.DefaultLoadingContext;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public abstract class RCPApplication implements IApplication {

	final public Object start(IApplicationContext context) throws Exception {
		Platform.endSplash();
		XWT.setLoadingContext(new DefaultLoadingContext(this.getClass().getClassLoader()));
		initialize();
		try {
			URL input = getInputURL();
			XWT.open(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Thread.currentThread().join();
		return IApplication.EXIT_OK;
	}

	abstract protected URL getInputURL();

	protected void initialize() {
	}

	public void stop() {
	}
}
