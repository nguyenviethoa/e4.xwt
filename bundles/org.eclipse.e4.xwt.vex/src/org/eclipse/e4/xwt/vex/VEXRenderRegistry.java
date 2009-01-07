package org.eclipse.e4.xwt.vex;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Composite;

public class VEXRenderRegistry {

	public static final String EXTENSION_ID = "org.eclipse.e4.xwt.vex.render";

	private VEXRenderRegistry() {
	}

	public static VEXRenderer getRender(Composite container) {
		VEXRenderer render = getRender();
		if (render != null && render instanceof AbstractVEXRenderer) {
			((AbstractVEXRenderer) render).setContainer(container);
		}
		return render;
	}

	public static VEXRenderer getRender() {
		IConfigurationElement[] configElem = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_ID);
		if (configElem != null && configElem.length > 0) {
			try {
				return (VEXRenderer) configElem[0].createExecutableExtension("class");
			} catch (CoreException e) {
				return null;
			}
		}
		return null;
	}
}
