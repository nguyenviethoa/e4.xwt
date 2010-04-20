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
package org.eclipse.e4.tools.ui.designer.wizards.part;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckable;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class PropertiesContentProvider implements IStructuredContentProvider {

	private static final String UNSETTABLES = "unsettables";
	private static final String SUPERS = "supers";
	private static final String COMPLEXIES = "complexies";

	// private boolean collectingUnsettables;
	// private boolean collectingComplexies;
	// private boolean collectingSupers;

	private Viewer viewer;
	private final Map<String, Object[]> checkableCache;
	private final Map<String, Boolean> options;

	private Object[] checkedElements;
	private ICheckStateListener listener;

	public PropertiesContentProvider() {
		this(false, true, true);
	}
	public PropertiesContentProvider(boolean collectingSupers,
			boolean collectingComplexies, boolean collectingUnsettables) {
		options = new HashMap<String, Boolean>();
		options.put(COMPLEXIES, collectingComplexies);
		options.put(SUPERS, collectingSupers);
		options.put(UNSETTABLES, collectingUnsettables);
		checkableCache = new HashMap<String, Object[]>();
	}
	public void dispose() {

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
		adapt();
	}

	private void adapt() {
		if (listener == null) {
			listener = new ICheckStateListener() {
				public void checkStateChanged(CheckStateChangedEvent event) {
					recordAll();
				}
			};
		}
		if (viewer instanceof ICheckable) {
			ICheckable checkable = (ICheckable) viewer;
			checkable.removeCheckStateListener(listener);
			checkable.addCheckStateListener(listener);
		}
	}

	public Object[] getElements(Object inputElement) {
		List<Object> properties = PDC.collectProperties(inputElement,
				isCollectingSupers(), isCollectingComplexies(),
				isCollectingUnsettables());
		return properties.toArray(new Object[properties.size()]);
	}

	public void setCollectingUnsettables(boolean collectingUnsets) {
		checkedElements = getRecores(UNSETTABLES);
		options.put(UNSETTABLES, collectingUnsets);
		updateViewer(UNSETTABLES);
	}

	public boolean isCollectingUnsettables() {
		return options.get(UNSETTABLES);
	}

	public void setCollectingComplexies(boolean collectingComplexies) {
		checkedElements = getRecores(COMPLEXIES);
		options.put(COMPLEXIES, collectingComplexies);
		updateViewer(COMPLEXIES);
	}

	public boolean isCollectingComplexies() {
		return options.get(COMPLEXIES);
	}

	public void setCollectingSupers(boolean collectingSupers) {
		checkedElements = getRecores(SUPERS);
		options.put(SUPERS, collectingSupers);
		updateViewer(SUPERS);
	}

	public boolean isCollectingSupers() {
		return options.get(SUPERS);
	}

	protected void updateViewer(String item) {
		if (viewer != null && viewer.getControl() != null
				&& !viewer.getControl().isDisposed()) {
			if (checkedElements == null) {
				recodeCheckable(item);
			}
			viewer.refresh();
			if (checkedElements != null) {
				setCheckedElements(viewer, checkedElements);
			}
		}
	}

	private void recodeCheckable(String item) {
		if (viewer != null && viewer instanceof ICheckable
				&& viewer.getControl() != null
				&& !viewer.getControl().isDisposed()) {
			String key = item + ":" + options.get(item);
			Object[] checked = getCheckedElements(viewer);
			checkableCache.put(key, checked);
		}
	}

	private void recordAll() {
		Object[] checked = getCheckedElements(viewer);
		Set<String> keySet = checkableCache.keySet();
		for (String key : keySet) {
			checkableCache.put(key, checked);
		}
	}

	private Object[] getRecores(String item) {
		String key = item + ":" + options.get(item);
		return checkableCache.get(key);
	}

	private Object[] getCheckedElements(Viewer viewer) {
		if (viewer instanceof CheckboxTableViewer) {
			return ((CheckboxTableViewer) viewer).getCheckedElements();
		} else if (viewer instanceof CheckboxTreeViewer) {
			return ((CheckboxTreeViewer) viewer).getCheckedElements();
		}
		return new Object[0];
	}

	private void setCheckedElements(Viewer viewer, Object[] checkedElements) {
		if (checkedElements == null) {
			return;
		}
		for (Object object : checkedElements) {
			if (object == null) {
				return;
			}
		}
		if (viewer instanceof CheckboxTableViewer) {
			((CheckboxTableViewer) viewer).setCheckedElements(checkedElements);
		} else if (viewer instanceof CheckboxTreeViewer) {
			((CheckboxTreeViewer) viewer).setCheckedElements(checkedElements);
		}
	}
}
