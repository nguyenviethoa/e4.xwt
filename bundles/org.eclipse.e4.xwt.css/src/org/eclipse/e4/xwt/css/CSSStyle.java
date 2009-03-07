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
 * This class handles the CSS style for XWT element. It can be used in two ways:
 * <ol>
 * <ul>
 * 1. Global style<br/>
 * XWT.addDefaultStyle()
 * </ul>
 * <ul>
 * 2. Inline style
 * 
 * <pre>
 * &lt;Composite&gt;
 *   &lt;Composite.Resources&gt;
 *     &lt;CSSStyle x:Key=&quot;style&quot; url=&quot;/test/style.css&quot;/&gt;
 *   &lt;/Composite.Resources&gt;
 *   &lt;Label text=&quot;Hello&quot;/&gt;
 * &lt;/Composite&gt;
 * </pre>
 * 
 * </ul>
 * </ol>
 */
public class CSSStyle implements IStyle {
	protected URL url;
	protected String content;

	private Method applyStyles;
	private Object engine;
	private Display display;

	private Class<?> jfaceViewerClass;
	private Method getControl;

	public CSSStyle() {
		this((String) null);
	}

	public CSSStyle(URL url) {
		this.url = url;
		try {
			jfaceViewerClass = Class.forName("org.eclipse.jface.viewers.Viewer"); //$NON-NLS-1$
			getControl = jfaceViewerClass.getMethod("getControl");
		} catch (Throwable e) {
		}
	}

	public CSSStyle(String content) {
		this.content = content;
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
				engineClass = loadClass("org.eclipse.e4.ui.css.nebula.engine.CSSNebulaEngineImpl"); //$NON-NLS-1$
			} catch (Throwable e) {
				engineClass = loadClass("org.eclipse.e4.ui.css.swt.engine.CSSSWTEngineImpl"); //$NON-NLS-1$
			}

			Constructor<?> ctor = engineClass.getConstructor(new Class[] { Display.class, Boolean.TYPE });
			engine = ctor.newInstance(new Object[] { display, Boolean.TRUE });

			Class<?> errorHandlerClass = loadClass("org.eclipse.e4.ui.css.core.engine.CSSErrorHandler"); //$NON-NLS-1$
			Method setErrorHandler = engineClass.getMethod("setErrorHandler", new Class[] { errorHandlerClass }); //$NON-NLS-1$
			Class<?> errorHandlerImplClass = loadClass("org.eclipse.e4.ui.css.core.impl.engine.CSSErrorHandlerImpl"); //$NON-NLS-1$
			setErrorHandler.invoke(engine, new Object[] { errorHandlerImplClass.newInstance() });

			Method urlResolver = null;
			try {
				Class<?> fileLocatorClass = loadClass("org.eclipse.core.runtime.FileLocator"); //$NON-NLS-1$
				urlResolver = fileLocatorClass.getMethod("resolve", new Class[] { URL.class }); //$NON-NLS-1$
			} catch (Throwable e) {
			}

			URL contentURL = url;
			if (urlResolver != null) {
				contentURL = (URL) urlResolver.invoke(null, new Object[] { contentURL });
			}

			InputStream stream = contentURL.openStream();
			Method parseStyleSheet = engineClass.getMethod("parseStyleSheet", new Class[] { InputStream.class }); //$NON-NLS-1$
			parseStyleSheet.invoke(engine, new Object[] { stream });
			stream.close();

			applyStyles = engineClass.getMethod("applyStyles", new Class[] { Object.class, Boolean.TYPE }); //$NON-NLS-1$
		} catch (Throwable e) {
			System.err.println("Warning - could not initialize CSS styling : " + e.toString()); //$NON-NLS-1$
		}
	}

	protected Class<?> loadClass(String className) throws ClassNotFoundException {
		try {
			return Class.forName(className); //$NON-NLS-1$
		} catch (ClassNotFoundException e) {
			return Thread.currentThread().getContextClassLoader().loadClass(className);
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		if (this.content == content || (this.content != null && this.content.equals(content))) {
			return;
		}
		this.content = content;
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
			if (name != null) {
				control.setData("org.eclipse.e4.ui.css.id", name);
			}
			try {
				applyStyles.invoke(engine, new Object[] { control, Boolean.FALSE });
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
