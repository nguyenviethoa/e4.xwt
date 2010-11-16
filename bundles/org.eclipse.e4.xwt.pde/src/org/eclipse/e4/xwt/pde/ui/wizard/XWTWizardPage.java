/*******************************************************************************
 * Copyright (c) 2006, 2010 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Soyatec and Valoueee - initial API and implementation
 *       http://www.eclipse.org/forums/index.php?t=msg&th=199619&start=0&S=eeee2217897168580b83685b5756bc21
 *******************************************************************************/
package org.eclipse.e4.xwt.pde.ui.wizard;

import java.net.URL;
import java.util.HashMap;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.XWTLoader;
import org.eclipse.e4.xwt.databinding.BindingContext;
import org.eclipse.jface.databinding.swt.ISWTObservable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public abstract class XWTWizardPage extends WizardPage {

	private Object dataContext;
	private BindingContext bindingContext;
	private AggregateValidationStatus validationStatus;

	private PageStateManager pageStateManager = new PageStateManager();
	
	class PageStateManager implements IChangeListener {
		public void handleChange(ChangeEvent event) {
			Object source = event.getSource();
			if (!(source instanceof ISWTObservable)) {
				setPageComplete(false);
			}
		}
	}

	/**
	 * @param _pageName
	 */
	protected XWTWizardPage(String _pageName, Object dataContext) {
		this(_pageName, null, null, dataContext);
	}

	protected XWTWizardPage(String pageName, String title,
			ImageDescriptor titleImage, Object dataContext) {
		this(pageName, title, titleImage, dataContext, null);
	}

	protected XWTWizardPage(String pageName, String title,
			ImageDescriptor titleImage, Object dataContext, BindingContext bindingContext) {
		super(pageName, title, null);
		this.dataContext = dataContext;
		this.bindingContext = bindingContext;
	}

	public void createControl(Composite _parent) {
		if (bindingContext == null) {
			bindingContext = new BindingContext(_parent);
		}
		validationStatus = bindingContext.getStatus();
		validationStatus.addChangeListener(new IChangeListener() {
			public void handleChange(ChangeEvent event) {
				IStatus status = (IStatus) validationStatus.getValue();
				setMessage(status.getMessage(), status.getSeverity());
			}
		});

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(getClassLoader());
			HashMap<String, Object> newOptions = new HashMap<String, Object>();
			newOptions.put(XWTLoader.CONTAINER_PROPERTY, _parent);
			Object dataContext = getDataContext();
			if (dataContext != null) {
				newOptions.put(XWTLoader.DATACONTEXT_PROPERTY, dataContext);
			}
			BindingContext bindingContext = getBindingContext();
			if (bindingContext != null) {
				newOptions.put(XWTLoader.BINDING_CONTEXT_PROPERTY,
						bindingContext);
			}

			Object element = XWT.loadWithOptions(getContentURL(), newOptions);
			setPageComplete(true);
			if (element instanceof Control) {
				Control control = (Control) element;
				setControl(control);
				XWT.addObservableChangeListener(control, pageStateManager);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Thread.currentThread().setContextClassLoader(classLoader);
			_parent.setVisible(true);
		}
	}
	
	@Override
	public void dispose() {
		Control control = getControl();
		if (control != null) {
			XWT.removeObservableChangeListener(control, pageStateManager);			
		}
		super.dispose();
	}

	abstract public URL getContentURL();

	protected ClassLoader getClassLoader() {
		return this.getClassLoader();
	}

	public Object getDataContext() {
		return dataContext;
	}

	public BindingContext getBindingContext() {
		return bindingContext;
	}
	
	public void setBindingContext(BindingContext bindingContext) {
		this.bindingContext = bindingContext;
	}
}
