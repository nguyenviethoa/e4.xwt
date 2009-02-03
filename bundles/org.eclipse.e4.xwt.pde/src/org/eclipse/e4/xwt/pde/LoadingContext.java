package org.eclipse.e4.xwt.pde;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.ILoadingContext;

public class LoadingContext implements ILoadingContext {
	protected ClassLoader classLoader;

	public LoadingContext(ClassLoader classLoader) {
		this.classLoader = classLoader;
		//			
		// new ClassLoader(classLoader) {
		// @Override
		// public URL getResource(String name) {
		// URL url = super.getResource(name);
		// if (url != null) {
		// try {
		// return FileLocator.resolve(url);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		// return url;
		// }
		// };
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public String getNamespace() {
		return IConstants.XWT_NAMESPACE;
	}
}
