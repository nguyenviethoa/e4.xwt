package org.eclipse.e4.xwt.pde.ui.views;

import java.io.InputStream;
import java.net.URL;

import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.pde.LoadingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public abstract class XWTEditorPart extends EditorPart {
	protected Composite container;
	protected Object dataContext;

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setInput(input);
		setSite(site);
	}

	@Override
	public void createPartControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		container.setLayout(new FillLayout());
		container.setBackgroundMode(SWT.INHERIT_DEFAULT);
		updateContent();
	}

	public void setContent(URL file) {
		XWT.setLoadingContext(new LoadingContext(this.getClass().getClassLoader()));

		for (Control child : container.getChildren()) {
			child.dispose();
		}

		try {
			// deviceAdapter = new DeviceAdapter(container) {
			// @Override
			// public Object getDataContext() {
			// return XAMLEditorPart.this.getDataContext();
			// }
			// };

			XWT.load(file);
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
			// return XAMLEditorPart.this.getDataContext();
			// }
			// };
			XWT.load(container, inputStream, base, getDataContext());
			container.layout(true, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Object getDataContext() {
		if (dataContext == null) {
			dataContext = createDataContext();
		}
		return dataContext;
	}

	/**
	 * Create the data context from IEditorInput
	 * 
	 * @return
	 */
	protected abstract Object createDataContext();

	/**
	 * update the editor content
	 */
	protected abstract void updateContent();
}
