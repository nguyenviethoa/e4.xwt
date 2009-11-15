package org.eclipse.e4.xwt.jface;

import java.net.URL;
import java.util.HashMap;

import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.XWTLoader;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractDialog extends Dialog {
	protected Object dataContext;
	protected String title;

	public AbstractDialog(Shell parentShell, String title, Object dataContext) {
		super(parentShell);
		this.dataContext = dataContext;
		this.title = title;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		if (title != null) {
			getShell().setText("Exif edition");
		}

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
			HashMap<String, Object> newOptions = new HashMap<String, Object>();
			newOptions.put(XWTLoader.CONTAINER_PROPERTY, parent);
			newOptions.put(XWTLoader.DATACONTEXT_PROPERTY, dataContext);
			newOptions.put(XWTLoader.CLASS_PROPERTY, this);
			Control control = XWT.loadWithOptions(getContentURL(), newOptions);
			GridLayoutFactory.fillDefaults().generateLayout(parent);
			parent.layout(true, true);
			return control;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Thread.currentThread().setContextClassLoader(classLoader);
			parent.setVisible(true);
		}
		return null;
	}

	protected abstract URL getContentURL();
}
