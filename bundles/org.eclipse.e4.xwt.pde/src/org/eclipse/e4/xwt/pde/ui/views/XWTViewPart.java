package org.eclipse.e4.xwt.pde.ui.views;

import java.io.InputStream;
import java.net.URL;

import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.pde.LoadingContext;
import org.eclipse.e4.xwt.pde.PDEPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.ViewPart;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view shows data obtained from the model. The sample creates a dummy model on the fly, but a real implementation would connect to the model available either in this or another plug-in (e.g. the workspace). The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be presented in the view. Each view can present the same model objects using different labels and icons, if needed. Alternatively, a single label provider can be shared between views in order to ensure that objects of the same type are presented in the same way everywhere.
 * <p>
 * 
 * @author yyang
 */
public abstract class XWTViewPart extends ViewPart {
	protected Composite container;
	protected Object dataContext;

	static {
		PDEPlugin.checkStartup();
	}

	/**
	 * The constructor.
	 */
	public XWTViewPart() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		container.setLayout(new FillLayout());
		container.setBackgroundMode(SWT.INHERIT_DEFAULT);

		updateContent();
	}

	abstract protected void updateContent();

	public void setContent(URL file) {
		XWT.setLoadingContext(new LoadingContext(this.getClass().getClassLoader()));

		for (Control child : container.getChildren()) {
			child.dispose();
		}

		try {
			XWT.load(container, file, getDataContext());
			container.layout(true, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setContent(InputStream inputStream, URL base) {
		XWT.setLoadingContext(new LoadingContext(this.getClass().getClassLoader()));

		for (Control child : container.getChildren()) {
			child.dispose();
		}

		try {
			// deviceAdapter = new DeviceAdapter(container) {
			// @Override
			// public Object getDataContext() {
			// return XAMLView.this.getDataContext();
			// }
			// };
			XWT.load(container, inputStream, base, getDataContext());
			container.layout(true, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}

	public Object getDataContext() {
		return dataContext;
	}

	public void setDataContext(Object dataContext) {
		this.dataContext = dataContext;
		updateContent();
	}
}