package org.eclipse.e4.xwt.css;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.e4.xwt.IStyle;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public class CCSStyle implements IStyle {
	protected String url;

	private Method applyStyles;
	private Object engine;
	private Display display;

	public void initialize(Display display) {
		this.display = display;
		// Instantiate SWT CSS Engine
		try {
			Class<?> engineClass = Class.forName("org.eclipse.e4.ui.css.nebula.engine.CSSNebulaEngineImpl"); //$NON-NLS-1$
			Constructor<?> ctor = engineClass.getConstructor(new Class[] { Display.class, Boolean.TYPE });
			engine = ctor.newInstance(new Object[] { display, Boolean.TRUE });
			display.setData("org.eclipse.e4.ui.css.core.engine", engine); //$NON-NLS-1$

			Class<?> errorHandlerClass = Class.forName("org.eclipse.e4.ui.css.core.engine.CSSErrorHandler"); //$NON-NLS-1$
			Method setErrorHandler = engineClass.getMethod("setErrorHandler", new Class[] { errorHandlerClass }); //$NON-NLS-1$
			Class<?> errorHandlerImplClass = Class.forName("org.eclipse.e4.ui.css.core.impl.engine.CSSErrorHandlerImpl"); //$NON-NLS-1$
			setErrorHandler.invoke(engine, new Object[] { errorHandlerImplClass.newInstance() });

			URL resolveUrl = FileLocator.resolve(new URL(url));
			display.setData("org.eclipse.e4.ui.css.core.cssURL", resolveUrl); //$NON-NLS-1$		

			InputStream stream = resolveUrl.openStream();
			Method parseStyleSheet = engineClass.getMethod("parseStyleSheet", new Class[] { InputStream.class }); //$NON-NLS-1$
			parseStyleSheet.invoke(engine, new Object[] { stream });
			stream.close();

			applyStyles = engineClass.getMethod("applyStyles", new Class[] { Object.class, Boolean.TYPE }); //$NON-NLS-1$
		} catch (Throwable e) {
			System.err.println("Warning - could not initialize CSS styling (but the applicationCSS property has a value) : " + e.toString()); //$NON-NLS-1$
		}
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		if (this.url == url || (this.url != null && this.url.equals(url))) {
			return;
		}
		this.url = url;
		reset();
	}

	protected void reset() {
		display = null;
	}

	public void applyStyle(Object target) {
		if (url == null || url.length() == 0) {
			return;
		}
		String name = XWT.getElementName(target);
		Control control = null;
		if (target instanceof Control) {
			control = (Control) target;
		} else if (target instanceof Viewer) {
			Viewer viewer = (Viewer) target;
			control = (Control) viewer.getControl();
		}
		if (control != null) {
			if (display == null) {
				initialize(control.getDisplay());
			}
			//
			// 
			control.setData("org.eclipse.e4.ui.css.CssClassName", name);
			control.setData("org.eclipse.e4.ui.css.id", name);
			try {
				applyStyles.invoke(engine, new Object[] { control, Boolean.FALSE });
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
