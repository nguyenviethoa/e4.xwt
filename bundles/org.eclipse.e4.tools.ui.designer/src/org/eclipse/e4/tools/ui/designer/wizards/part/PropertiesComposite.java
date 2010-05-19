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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class PropertiesComposite {

	private PartDataContext dataContext;
	private CheckboxTableViewer tableViewer;
	private PropertiesContentProvider propertiesProvider;

	private Control control;

	public PropertiesComposite(Composite parent, PartDataContext dataContext,
			boolean expandable) {
		this.dataContext = dataContext;
		if (expandable) {
			ExpandableComposite eComp = new ExpandableComposite(parent,
					SWT.NONE, ExpandableComposite.TWISTIE
							| ExpandableComposite.CLIENT_INDENT
							| ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT);
			createControl(eComp);
			this.control = eComp;
		} else {
			this.control = createClient(parent);
		}
	}

	public Control getControl() {
		return control;
	}

	protected void createControl(final ExpandableComposite expandableComp) {
		expandableComp.setText("Properties");
		expandableComp.titleBarTextMarginWidth = 0;

		Composite client = createClient(expandableComp);
		expandableComp.marginWidth = 0;
		expandableComp.setClient(client);

		expandableComp.setExpanded(true);
		expandableComp.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				expandableComp.getParent()
						.layout(new Control[]{expandableComp});
			}
		});
	}

	protected Composite createClient(Composite parent) {
		Composite client = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		client.setLayout(layout);

		Label label = new Label(client, SWT.NONE);
		label.setText("Show Members:");

		Composite tableComp = new Composite(client, SWT.NONE);
		TableColumnLayout tableLayout = new TableColumnLayout();
		tableComp.setLayout(tableLayout);
		tableComp.setLayoutData(GridDataFactory.fillDefaults().grab(true, true)
				.create());
		
		tableViewer = CheckboxTableViewer.newCheckList(tableComp, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		propertiesProvider = new PropertiesContentProvider();
		tableViewer.setContentProvider(propertiesProvider);

		final Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn propertyColumn = new TableColumn(table, SWT.CENTER);
		propertyColumn.reskin(0);
		propertyColumn.setText("Property");
		tableLayout.setColumnData(propertyColumn, new ColumnWeightData(10));
		TableViewerColumn propertyViewerColumn = new TableViewerColumn(
				tableViewer, propertyColumn);
		final PDCTypeLabelProvider labelProvider = new PDCTypeLabelProvider();
		propertyViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return PDC.getPropertyDisplayName(element);
			}
			public Image getImage(Object element) {
				return labelProvider.getImage(element);
			}
		});

		TableColumn masterColumn = new TableColumn(table, SWT.LEFT);
		masterColumn.setText("Selection Provider");
		tableLayout.setColumnData(masterColumn, new ColumnWeightData(10));

		TableViewerColumn masterViewerColumn = new TableViewerColumn(
				tableViewer, masterColumn);
		masterViewerColumn.setLabelProvider(new CellLabelProvider() {
			public void update(ViewerCell cell) {
				final TableItem item = (TableItem) cell.getItem();
				DisposeListener listener = new DisposeListener() {

					public void widgetDisposed(DisposeEvent e) {
						if (item.getData("EDITOR") != null) {
							TableEditor editor = (TableEditor) item
									.getData("EDITOR");
							editor.getEditor().dispose();
							editor.dispose();
						}
					}

				};

				if (item.getData("EDITOR") != null) {
					TableEditor editor = (TableEditor) item.getData("EDITOR");
					if (editor.getEditor() != null) {
						editor.getEditor().dispose();
					}
					editor.dispose();
				}

				if (item.getData("DISPOSELISTNER") != null) {
					item.removeDisposeListener((DisposeListener) item
							.getData("DISPOSELISTNER"));
				}
				final Object data = item.getData();
				if (!dataContext.isPropertyMany(data)) {
					return;
				}
				TableEditor editor = new TableEditor(item.getParent());
				item.setData("EDITOR", editor);
				final Button checkBox = new Button(item.getParent(), SWT.CHECK);
				checkBox.pack();
				checkBox.setEnabled(dataContext.containsProperty(data));
				checkBox.setBackground(table.getBackground());
				checkBox.setSelection(dataContext.containsMaster(data));
				checkBox.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event event) {
						if (checkBox.getSelection()) {
							dataContext.addMasterProperty(data);
						} else {
							dataContext.removeMasterProperty(data);
						}
					}
				});
				editor.grabHorizontal = true;
				editor.setEditor(checkBox, item, 1);

				item.addDisposeListener(listener);
				item.setData("DISPOSELISTNER", listener);
			}
		});
		tableViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				Object element = event.getElement();
				boolean checked = event.getChecked();
				TableItem item = (TableItem) tableViewer.testFindItem(element);
				if (item.getData("EDITOR") != null) {
					TableEditor editor = (TableEditor) item.getData("EDITOR");
					Button control = (Button) editor.getEditor();
					if (control != null) {
						control.setEnabled(checked);
						if (!checked && control.getSelection()) {
							dataContext.removeMasterProperty(element);
						} else if (checked && control.getSelection()) {
							dataContext.addMasterProperty(element);
						}
					}
				}
				if (checked) {
					dataContext.addProperty(element);
				} else {
					dataContext.removeProperty(element);
				}
			}
		});

		resetTable();

		dataContext.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				String propertyName = evt.getPropertyName();
				if (PartDataContext.VALUE.equals(propertyName)
						|| PartDataContext.TYPE.equals(propertyName)) {
					resetTable();
				}
			}
		});

		createOptions(client);
		return client;
	}

	protected void resetTable() {
		if (tableViewer == null || tableViewer.getControl() == null
				|| tableViewer.getControl().isDisposed()) {
			return;
		}
		tableViewer.setInput(dataContext.getType());
		List<Object> properties = dataContext.getProperties();
		if (properties != null && properties.size() > 0) {
			tableViewer.setCheckedElements(properties
					.toArray(new Object[properties.size()]));
		}
	}

	protected void createOptions(Composite parent) {
		final Button hideComplexButton = new Button(parent, SWT.CHECK);
		hideComplexButton.setText("Hide Complex properties of type.");
		hideComplexButton.setSelection(!propertiesProvider
				.isCollectingComplexies());
		hideComplexButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				propertiesProvider.setCollectingComplexies(!hideComplexButton
						.getSelection());
			}
		});

		final Button hideSuperButton = new Button(parent, SWT.CHECK);
		hideSuperButton.setText("Hide Super properties of type.");
		hideSuperButton.setSelection(!propertiesProvider.isCollectingSupers());
		hideSuperButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				propertiesProvider.setCollectingSupers(!hideSuperButton
						.getSelection());
			}
		});

		final Button hideUnsetButton = new Button(parent, SWT.CHECK);
		hideUnsetButton.setText("Hide Unsettable properties of type.");
		hideUnsetButton.setSelection(!propertiesProvider
				.isCollectingUnsettables());
		hideUnsetButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				propertiesProvider.setCollectingUnsettables(!hideUnsetButton
						.getSelection());
			}
		});

		updateOptionsStatus(hideSuperButton, hideUnsetButton);

		dataContext.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (PartDataContext.TYPE.equals(evt.getPropertyName())) {
					updateOptionsStatus(hideSuperButton, hideUnsetButton);
				}
			}
		});
	}

	private void updateOptionsStatus(final Button hideSuperButton,
			final Button hideUnsetButton) {
		Object type = dataContext.getType();
		if (type instanceof Class<?>) {
			hideUnsetButton.setEnabled(false);
			hideSuperButton.setEnabled(true);
		} else if (type instanceof EClass) {
			hideUnsetButton.setEnabled(true);
			hideSuperButton.setEnabled(false);
		}
	}

	public static Composite create(Composite parent, PartDataContext dataContext) {
		PropertiesComposite pc = new PropertiesComposite(parent, dataContext,
				false);
		return (Composite) pc.getControl();
	}

	public static ExpandableComposite createExpandabel(Composite parent,
			PartDataContext dataContext) {
		PropertiesComposite pc = new PropertiesComposite(parent, dataContext,
				true);
		return (ExpandableComposite) pc.getControl();
	}
}
