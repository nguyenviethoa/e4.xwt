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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class SingleCheckBoxTreeViewer extends CheckboxTreeViewer {

	private TreeItem checkedRootItem;

	public SingleCheckBoxTreeViewer(Composite parent, int style) {
		super(parent, style);
		addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				handleCheckStateEvent(event);
			}
		});
		addTreeListener(new ITreeViewerListener() {
			public void treeExpanded(TreeExpansionEvent event) {
				Object element = event.getElement();
				TreeItem treeItem = (TreeItem) testFindItem(element);
				if (treeItem != null) {
					handleChecked(treeItem, treeItem.getChecked());
				}
			}

			public void treeCollapsed(TreeExpansionEvent event) {

			}
		});
	}

	protected void handleCheckStateEvent(CheckStateChangedEvent event) {
		boolean checked = event.getChecked();
		Object element = event.getElement();
		TreeItem item = (TreeItem) testFindItem(element);
		handleChecked(item, checked);
	}

	private void handleChecked(TreeItem item, boolean checked) {
		TreeItem parentItem = item.getParentItem();
		if (parentItem == null) {
			if (checked && checkedRootItem != null && checkedRootItem != item) {
				_setChecked(checkedRootItem, false);
				_setSubtreeChecked(checkedRootItem, false);
			}
			_setGrayed(item, false);
			_setSubtreeChecked(item, checked);
		} else {
			_setGrayed(item, false);
			_setSubtreeChecked(item, checked);

			while (parentItem != null) {
				checkParentItem(parentItem);
				parentItem = parentItem.getParentItem();
			}
		}
		TreeItem[] items = getTree().getItems();
		for (TreeItem treeItem : items) {
			if (treeItem.getData() == null) {
				continue;
			}
			if (treeItem.getChecked()) {
				checkedRootItem = treeItem;
				break;
			}
		}
	}

	private void checkParentItem(TreeItem item) {
		boolean isAllChecked = true;
		boolean hasChecks = false;
		TreeItem[] items = item.getItems();
		for (TreeItem object : items) {
			boolean childChecked = object.getChecked();
			isAllChecked &= childChecked && !object.getGrayed();
			if (childChecked) {
				hasChecks = true;
			}
		}
		_setGrayed(item, !isAllChecked);
		_setChecked(item, hasChecks);
	}

	private void _setGrayed(TreeItem item, boolean grayed) {
		if (item != null && !item.isDisposed() && item.getData() != null) {
			item.setGrayed(grayed);
		}
	}

	private void _setSubtreeChecked(TreeItem item, boolean checked) {
		if (item == null || item.isDisposed() || item.getData() == null) {
			return;
		}
		Item[] items = getChildren(item);
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				Item it = items[i];
				if (it.getData() != null && (it instanceof TreeItem)) {
					TreeItem treeItem = (TreeItem) it;
					_setChecked(treeItem, checked);
					_setSubtreeChecked(treeItem, checked);
				}
			}
		}
	}

	private void _setChecked(TreeItem item, boolean checked) {
		if (item == null || item.isDisposed() || item.getData() == null) {
			return;
		}
		item.setChecked(checked);
	}

	public Object getChecked() {
		if (checkedRootItem == null) {
			TreeItem[] items = getTree().getItems();
			for (TreeItem treeItem : items) {
				if (treeItem.getChecked() && treeItem.getData() != null) {
					checkedRootItem = treeItem;
					break;
				}
			}
		}
		if (checkedRootItem != null) {
			return checkedRootItem.getData();
		}
		return null;
	}

	public List<Object> getCheckedChildren() {
		if (checkedRootItem == null) {
			TreeItem[] items = getTree().getItems();
			for (TreeItem treeItem : items) {
				if (treeItem.getChecked() && treeItem.getData() != null) {
					checkedRootItem = treeItem;
					break;
				}
			}
		}
		if (checkedRootItem == null) {
			return Collections.emptyList();
		}
		List<Object> children = new ArrayList<Object>();
		Item[] items = getChildren(checkedRootItem);
		if (items != null) {
			for (Item item : items) {
				if (item == null || item.getData() == null) {
					continue;
				}
				children.add(item.getData());
			}
		}
		return children;
	}

}
