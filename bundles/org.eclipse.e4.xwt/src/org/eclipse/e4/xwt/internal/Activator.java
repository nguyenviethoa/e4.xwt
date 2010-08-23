package org.eclipse.e4.xwt.internal;

import org.eclipse.e4.xwt.IXWTInitializer;
import org.eclipse.e4.xwt.IXWTLoader;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.XWTLoaderManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator, IXWTInitializer {

	private boolean initialized;

	public void start(BundleContext context) throws Exception {
		initialized = false;
		XWT.addInitializer(this);
		XWT.runInDisplay(new Runnable() {
			public void run() {
				XWTLoaderManager.getDefault();
			}
		});
	}

	public void initialize(IXWTLoader loader) {
		initialized = true;		
	}

	public void stop(BundleContext context) throws Exception {
	}
	
	public boolean isInitialized() {
		return initialized;
	}
}
