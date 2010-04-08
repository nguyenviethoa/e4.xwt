/*******************************************************************************
 * Copyright (c) 2006, 2009 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.ui.designer.wizards;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class PropertiesComposite extends ExpandableComposite {

	private SingleCheckBoxTreeViewer propertiesViewer;

	private Object dataContext;

	private CommonContentProvider contentProvider;

	private ToolItem hideSuperTypeItem;

	private ToolItem hideComplexTypeItem;

	public PropertiesComposite(Composite parent, Object dataContext) {
		super(parent, SWT.NONE, ExpandableComposite.TWISTIE
				| ExpandableComposite.CLIENT_INDENT);
		this.dataContext = dataContext;
		createControl(this);
	}

	private void createControl(final ExpandableComposite composite) {
		contentProvider = new CommonContentProvider(true, false);

		ToolBar toolBar = new ToolBar(composite, SWT.NONE);

		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				contentProvider.setDisplayComplexTypes(!hideComplexTypeItem
						.getSelection());
				contentProvider.setDisplaySuperTypes(!hideSuperTypeItem
						.getSelection());
				propertiesViewer.refresh();
			}
		};

		hideComplexTypeItem = new ToolItem(toolBar, SWT.CHECK);
		hideComplexTypeItem.setToolTipText("Hide Complex Properties");
		hideComplexTypeItem.setImage(JavaPluginImages
				.get(JavaPluginImages.IMG_MISC_PROTECTED));
		hideComplexTypeItem.setSelection(!contentProvider
				.isDisplayComplexTypes());
		hideComplexTypeItem.setEnabled(false);
		hideComplexTypeItem.addListener(SWT.Selection, listener);

		hideSuperTypeItem = new ToolItem(toolBar, SWT.CHECK);
		hideSuperTypeItem.setToolTipText("Hide Properties from Super Types");
		hideSuperTypeItem.setImage(JavaPluginImages
				.get(JavaPluginImages.IMG_MISC_PUBLIC));
		hideSuperTypeItem.setSelection(!contentProvider.isDisplaySuperTypes());
		hideSuperTypeItem.setEnabled(false);
		hideSuperTypeItem.addListener(SWT.Selection, listener);

		composite.setText("Properties");
		composite.setTextClient(toolBar);
		composite.titleBarTextMarginWidth = 0;
		composite.setExpanded(dataContext != null);
		composite.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				composite.getParent().layout(new Control[]{composite});
				hideComplexTypeItem.setEnabled(composite.isExpanded());
				hideSuperTypeItem.setEnabled(composite.isExpanded());
			}
		});

		propertiesViewer = new SingleCheckBoxTreeViewer(composite, SWT.BORDER
				| SWT.V_SCROLL | SWT.H_SCROLL);
		propertiesViewer.setContentProvider(contentProvider);
		propertiesViewer.setLabelProvider(new CommonLabelProvider());
		propertiesViewer.setAutoExpandLevel(2);
		setInput(dataContext);
		composite.setClient(propertiesViewer.getTree());
		composite.getDisplay().asyncExec(new Runnable() {
			public void run() {
				composite.setExpanded(false);
				composite.getParent().layout(new Control[]{composite});
			}
		});
	}

	/**
	 * @param dataContext
	 *            the dataContext to set
	 */
	public void setDataContext(Object dataContext) {
		this.dataContext = dataContext;
		setInput(dataContext);
	}

	private void setInput(Object input) {
		if (propertiesViewer == null || propertiesViewer.getTree().isDisposed()) {
			return;
		}
		propertiesViewer.setInput(input);
		propertiesViewer.setAllChecked(true);
	}

	public List<String> getProperties() {
		List<String> properties = new ArrayList<String>();
		List<Object> checkedChildren = propertiesViewer.getCheckedChildren();
		for (Object object : checkedChildren) {
			if (object instanceof PropertyDescriptor) {
				properties.add(((PropertyDescriptor) object).getName());
			} else if (object instanceof EStructuralFeature) {
				properties.add(((EStructuralFeature) object).getName());
			}
		}
		return properties;
	}

	/**
	 * @return the dataContext
	 */
	public Object getDataContext() {
		return dataContext;
	}

}
