package org.eclipse.e4.xwt.css;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;

import org.eclipse.e4.xwt.IStyle;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.XWTException;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/**
 * <Composite> <Composite.Resources> <CSSStyle x:Key="style" url="/test/style.css"/> </Composite.Resources> <Label text="Hello"/> </Composite>
 */
public class CSSStyle implements IStyle {
	protected URL url;

	private Method applyStyles;
	private Object engine;
	private Display display;

	private Class<?> jfaceViewerClass;
	private Method getControl;

	public CSSStyle() {
		this(null);
	}

	public CSSStyle(URL url) {
		this.url = url;
		try {
			jfaceViewerClass = Class.forName("org.eclipse.jface.viewers.Viewer"); //$NON-NLS-1$
			getControl = jfaceViewerClass.getMethod("getControl");
		} catch (Throwable e) {
		}
	}

	public void initialize(Display display) {
		if (this.display != null && this.display == display) {
			return;
		}
		this.display = display;
		// Instantiate SWT CSS Engine
		try {
			Class<?> engineClass = null;

			try {
				engineClass = Class.forName("org.eclipse.e4.ui.css.nebula.engine.CSSNebulaEngineImpl"); //$NON-NLS-1$
			} catch (Throwable e) {
				engineClass = Class.forName("org.eclipse.e4.ui.css.swt.engine.CSSSWTEngineImpl"); //$NON-NLS-1$
			}

			Constructor<?> ctor = engineClass.getConstructor(new Class[] { Display.class, Boolean.TYPE });
			engine = ctor.newInstance(new Object[] { display, Boolean.TRUE });
			display.setData("org.eclipse.e4.ui.css.core.engine", engine); //$NON-NLS-1$

			Class<?> errorHandlerClass = Class.forName("org.eclipse.e4.ui.css.core.engine.CSSErrorHandler"); //$NON-NLS-1$
			Method setErrorHandler = engineClass.getMethod("setErrorHandler", new Class[] { errorHandlerClass }); //$NON-NLS-1$
			Class<?> errorHandlerImplClass = Class.forName("org.eclipse.e4.ui.css.core.impl.engine.CSSErrorHandlerImpl"); //$NON-NLS-1$
			setErrorHandler.invoke(engine, new Object[] { errorHandlerImplClass.newInstance() });

			Method urlResolver = null;
			try {
				Class<?> fileLocatorClass = Class.forName("org.eclipse.core.runtime.FileLocator"); //$NON-NLS-1$
				urlResolver = fileLocatorClass.getMethod("resolve", new Class[] { URL.class }); //$NON-NLS-1$
			} catch (Throwable e) {
			}

			URL contentURL = url;
			if (urlResolver != null) {
				contentURL = (URL) urlResolver.invoke(null, new Object[] { contentURL });
			}
			display.setData("org.eclipse.e4.ui.css.core.cssURL", contentURL); //$NON-NLS-1$		

			InputStream stream = contentURL.openStream();
			Method parseStyleSheet = engineClass.getMethod("parseStyleSheet", new Class[] { InputStream.class }); //$NON-NLS-1$
			parseStyleSheet.invoke(engine, new Object[] { stream });
			stream.close();

			applyStyles = engineClass.getMethod("applyStyles", new Class[] { Object.class, Boolean.TYPE }); //$NON-NLS-1$
		} catch (Throwable e) {
			System.err.println("Warning - could not initialize CSS styling : " + e.toString()); //$NON-NLS-1$
		}
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
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
		if (url == null) {
			return;
		}

		String name = XWT.getElementName(target);
		Control control = null;
		if (target instanceof Control) {
			control = (Control) target;
		} else if (getControl != null && jfaceViewerClass.isInstance(target)) {
			try {
				control = (Control) getControl.invoke(target);
			} catch (Throwable e) {
				throw new XWTException(e);
			}
		}
		if (control != null) {
			initialize(control.getDisplay());
			control.setData("org.eclipse.e4.ui.css.id", name);
			control.setData("org.eclipse.e4.ui.css.CssClassName", "properties");
			try {
				applyStyles.invoke(engine, new Object[] { control, Boolean.FALSE });
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
