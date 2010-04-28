/*******************************************************************************
 * Copyright (c) 2006, 2010 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.ui.designer.widgets;

import java.text.Collator;
import java.util.Comparator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.tools.ui.designer.E4DesignerPlugin;
import org.eclipse.e4.tools.ui.designer.utils.ApplicationModelHelper;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public abstract class AbstractFindElementsDialog extends
		FilteredItemsSelectionDialog {

	private Object[] initializeElements;
	private String detailsLabelText;
	private ILabelProvider labelProvider;

	public AbstractFindElementsDialog(Shell shell, Object[] initializeElements,
			String detailsLabelText) {
		super(shell, false);
		this.initializeElements = initializeElements;
		this.detailsLabelText = detailsLabelText;
		setTitle("Find Element Dialog");
		setInitialPattern("?");
		setListLabelProvider(labelProvider = ApplicationModelHelper
				.getLabelProvider());
		setDetailsLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				return getFilterForeignText(element);
			}
		});
	}

	protected Label createMessageArea(Composite composite) {
		Label msgLabel = super.createMessageArea(composite);
		msgLabel.setFont(JFaceResources.getBannerFont());
		return msgLabel;
	}

	protected Control createExtendedContentArea(Composite parent) {
		if (detailsLabelText != null) {
			Label label = new Label(parent, SWT.NONE);
			label.setText(detailsLabelText);
			label.setForeground(parent.getDisplay().getSystemColor(
					SWT.COLOR_RED));
			return label;
		}
		return null;
	}

	protected IDialogSettings getDialogSettings() {
		return E4DesignerPlugin.getDefault().getDialogSettings();
	}

	protected IStatus validateItem(Object item) {
		return Status.OK_STATUS;
	}

	protected ItemsFilter createFilter() {
		return new ItemsFilter() {
			public boolean matchItem(Object item) {
				return matches(getFilterForeignText(item));
			}

			public boolean isConsistentItem(Object item) {
				return true;
			}
		};
	}

	protected abstract String getFilterForeignText(Object item);

	protected Comparator getItemsComparator() {
		return new Comparator() {
			public int compare(Object o1, Object o2) {
				String t1 = labelProvider.getText(o1);
				String t2 = labelProvider.getText(o2);
				return Collator.getInstance().compare(t1, t2);
			}
		};
	}

	protected void fillContentProvider(AbstractContentProvider contentProvider,
			ItemsFilter itemsFilter, IProgressMonitor progressMonitor)
			throws CoreException {
		if (initializeElements != null) {
			for (Object object : initializeElements) {
				contentProvider.add(object, itemsFilter);
			}
		}
	}

	public String getElementName(Object item) {
		return ApplicationModelHelper.getText(item);
	}

}
