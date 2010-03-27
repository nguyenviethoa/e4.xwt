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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.tools.ui.designer.E4DesignerPlugin;
import org.eclipse.e4.tools.ui.designer.utils.XWTCodegen;
import org.eclipse.e4.tools.ui.designer.wizards.NewFileInputPartWizard.DataContext;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreAdapterFactory;
import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class NewFileInputSelectionWizardPage extends WizardPage {

	private static final String INPUT_NULL_ERROR = "Input file is empty.";
	private static final String EOBJECT_NULL_ERROR = "Selected type for data context is empty.";

	// columnProperties
	private static final String COL_PROP_ITEMS = "Items";
	private static final String COL_PROP_MASTER = "Master/Details";

	private Label inputLabel;
	private Text inputText;
	private Button browserButton;
	private CheckboxTreeViewer treeViewer;

	private IFile inputFile;
	private EObject selection;

	private EcoreAdapterFactory factory;
	private AdapterFactoryItemDelegator adapter;
	private ResourceContentProvider contentProvider;

	private DataContext dataContext;

	protected NewFileInputSelectionWizardPage(DataContext dataContext) {
		super("NewFileInputSelectionWizardPage");
		this.dataContext = dataContext;

		setTitle("File Selection");
		setMessage("Choose a *.* file with EMF dynamic models.");

		factory = new EcoreItemProviderAdapterFactory();
		adapter = new AdapterFactoryItemDelegator(factory);
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		Composite control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout(3, false));

		inputLabel = new Label(control, SWT.NONE);
		inputLabel.setText("Input");

		inputText = new Text(control, SWT.BORDER);
		inputText.setLayoutData(GridDataFactory.fillDefaults()
				.grab(true, false).create());

		browserButton = new Button(control, SWT.NONE);
		browserButton.setText("Browser...");
		browserButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				chooseInput();
			}
		});

		Composite treeComp = new Composite(control, SWT.NONE);
		treeComp.setLayoutData(GridDataFactory.fillDefaults().grab(true, true)
				.span(3, 1).create());
		TreeColumnLayout tcl = new TreeColumnLayout();
		treeComp.setLayout(tcl);
		treeViewer = new CheckboxTreeViewer(treeComp, SWT.FULL_SELECTION
				| SWT.BORDER);
		Tree tree = treeViewer.getTree();
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		TreeViewerColumn itemsColumn = new TreeViewerColumn(treeViewer,
				SWT.CENTER);
		itemsColumn.getColumn().setText("Items");
		tcl.setColumnData(itemsColumn.getColumn(), new ColumnWeightData(10));

		TreeViewerColumn masterDetailsColumn = new TreeViewerColumn(treeViewer,
				SWT.CENTER);
		masterDetailsColumn.getColumn().setText("Master/Details");
		tcl.setColumnData(masterDetailsColumn.getColumn(),
				new ColumnWeightData(10));
		treeViewer.setColumnProperties(new String[]{COL_PROP_ITEMS,
				COL_PROP_MASTER});

		CellEditor[] editors = new CellEditor[2];
		editors[1] = new CheckBoxCellEditor(tree);
		treeViewer.setCellEditors(editors);
		treeViewer.setCellModifier(new ICellModifier() {
			public void modify(Object element, String property, Object value) {
				if (element instanceof TreeItem) {
					element = ((TreeItem) element).getData();
				}
				if (COL_PROP_MASTER.equals(property)
						&& element instanceof FeatureValue
						&& ((FeatureValue) element).isMany()) {
					Boolean useMaster = Boolean.valueOf(value.toString());
					if (useMaster) {
						dataContext.getMasterFeatures().add(
								((FeatureValue) element).feature);
					} else {
						dataContext.getMasterFeatures().remove(
								((FeatureValue) element).feature);
					}
					((FeatureValue) element).setMaster(useMaster);
				}
				treeViewer.refresh(element, true);
			}

			public Object getValue(Object element, String property) {
				if (COL_PROP_MASTER.equals(property)
						&& element instanceof FeatureValue
						&& ((FeatureValue) element).isMany()) {
					return dataContext.getMasterFeatures().contains(
							((FeatureValue) element).feature);
				}
				return null;
			}

			public boolean canModify(Object element, String property) {
				if (COL_PROP_MASTER.equals(property)
						&& element instanceof FeatureValue) {
					return ((FeatureValue) element).isMany();
				}
				return false;
			}
		});

		treeViewer
				.setContentProvider(contentProvider = new ResourceContentProvider());
		treeViewer.setLabelProvider(new ColumnableLableProvider());

		treeViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				boolean checked = event.getChecked();
				Object element = event.getElement();
				setChecked(element, checked);
			}
		});

		final ViewerFilter filter = new ViewerFilter() {
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				if (element instanceof FeatureValue) {
					return ((FeatureValue) element).value != null;
				}
				return true;
			}
		};
		treeViewer.addFilter(filter);
		final Button filterButton = new Button(control, SWT.CHECK);
		filterButton.setText("Hide unsettable features.");
		filterButton.setSelection(true);
		filterButton.setLayoutData(GridDataFactory.fillDefaults().span(3, 1)
				.grab(true, false).create());
		filterButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (filterButton.getSelection()) {
					treeViewer.addFilter(filter);
				} else {
					treeViewer.removeFilter(filter);
				}
				if (selection != null) {
					setChecked(selection, true);
				}
			}
		});
		setControl(control);
		Dialog.applyDialogFont(control);

		setInput(null);
	}
	protected void chooseInput() {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				getShell(), new WorkbenchLabelProvider(),
				new WorkbenchContentProvider());
		dialog.setTitle("Input Selection Dialog");
		dialog.setMessage("Choose a file with emf models as input.");
		dialog.setAllowMultiple(false);
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.setValidator(new ISelectionStatusValidator() {
			public IStatus validate(Object[] selection) {
				if (selection == null || selection.length != 1) {
					return new Status(IStatus.ERROR,
							E4DesignerPlugin.PLUGIN_ID,
							"Invalid selection items.");
				}
				Object sel = selection[0];
				if (!(sel instanceof IFile)) {
					return new Status(IStatus.ERROR,
							E4DesignerPlugin.PLUGIN_ID,
							"Selection is not a File.");
				}
				return Status.OK_STATUS;
			}
		});
		dialog.addFilter(new ViewerFilter() {
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				if (element instanceof IProject) {
					return ((IProject) element).isOpen();
				}
				return true;
			}
		});
		if (Window.OK != dialog.open()) {
			setInput(null);
		} else {
			setInput((IFile) dialog.getResult()[0]);
		}
	}

	private void setInput(IFile newInput) {
		this.inputFile = newInput;
		if (inputFile == null) {
			inputText.setText("");
			setErrorMessage(INPUT_NULL_ERROR);
			treeViewer.setInput(null);
		} else {
			inputText.setText(inputFile.getFullPath().toString());
			String errorMessage = getErrorMessage();
			if (INPUT_NULL_ERROR.equals(errorMessage)) {
				setErrorMessage(null);
			}
			ResourceSet rs = new ResourceSetImpl();
			try {
				URI uri = URI.createPlatformPluginURI(inputFile.getFullPath()
						.toString(), true);
				Resource resource = rs.getResource(uri, true);
				treeViewer.setInput(resource);
			} catch (Exception e) {
				try {
					URI uri = URI.createPlatformResourceURI(inputFile
							.getFullPath().toString(), true);
					Resource resource = rs.getResource(uri, true);
					treeViewer.setInput(resource);
				} catch (Exception e1) {
					try {
						URI uri = URI.createFileURI(inputFile.getLocation()
								.toString());
						Resource resource = rs.getResource(uri, true);
						treeViewer.setInput(resource);
					} catch (Exception e2) {
						treeViewer.setInput(null);
					}
				}
			}
		}
		validateSelection();
	}

	private void validateSelection() {
		String errorMessage = getErrorMessage();
		if (errorMessage == null || errorMessage.equals(EOBJECT_NULL_ERROR)) {
			if (selection == null) {
				setErrorMessage(EOBJECT_NULL_ERROR);
			} else {
				setErrorMessage(null);
			}
		}
		try {
			getContainer().updateButtons();
		} catch (Exception e) {
			// maybe buttons not created yet.
		}
	}

	public boolean canFlipToNextPage() {
		if (selection != null && getErrorMessage() == null) {
			buildDataContext();
			return true;
		}
		return false;
	}
	private void buildDataContext() {
		Object[] checkedElements = treeViewer.getCheckedElements();
		dataContext.clear();
		dataContext.setInput(inputFile);
		dataContext.setEObject(selection);
		for (Object object : checkedElements) {
			if (object instanceof FeatureValue
					&& selection == ((FeatureValue) object).parent) {
				dataContext.getFeatures().add(((FeatureValue) object).feature);
			}
		}
	}

	private void setChecked(Object element, boolean checked) {
		if (element instanceof EObject) {
			if (checked && selection != null) {
				treeViewer.setChecked(selection, false);
				treeViewer.setSubtreeChecked(selection, false);
			}
			treeViewer.setGrayed(element, false);
			treeViewer.setSubtreeChecked(element, checked);
			if (checked) {
				selection = (EObject) element;
			} else {
				selection = null;
			}
		} else if (element instanceof FeatureValue) {
			EObject parent = ((FeatureValue) element).parent;
			if (checked && selection != null && selection != parent) {
				treeViewer.setChecked(selection, false);
				treeViewer.setSubtreeChecked(selection, false);
			}
			boolean isAllChecked = true;
			boolean hasChecks = false;
			Object[] children = contentProvider.getChildren(parent);
			for (Object object : children) {
				boolean childChecked = treeViewer.getChecked(object);
				Widget item = treeViewer.testFindItem(object);
				if (item == null) {
					continue;// filtered.
				}
				isAllChecked &= childChecked;
				if (childChecked) {
					hasChecks = true;
				}
			}
			treeViewer.setGrayed(parent, !isAllChecked);
			treeViewer.setChecked(parent, hasChecks);
			if (hasChecks) {
				selection = parent;
			} else {
				selection = null;
			}
		}

		validateSelection();
	}

	private class ResourceContentProvider implements ITreeContentProvider {
		private Map<Object, Object[]> childrenMap = new HashMap<Object, Object[]>();
		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		public void dispose() {

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}

		public Object[] getChildren(Object parentElement) {
			Object[] children = childrenMap.get(parentElement);
			if (children == null) {
				if (parentElement instanceof Resource) {
					children = ((Resource) parentElement).getContents()
							.toArray(new Object[0]);
				} else if (parentElement instanceof EObject) {
					EObject eObj = (EObject) parentElement;
					List<Object> settings = new ArrayList<Object>();
					EList<EStructuralFeature> features = eObj.eClass()
							.getEStructuralFeatures();
					for (EStructuralFeature feature : features) {
						if (eObj.eIsSet(feature)) {
							Object eGet = eObj.eGet(feature);
							settings.add(new FeatureValue(eObj, feature, eGet));
						} else {
							settings.add(new FeatureValue(eObj, feature, null));
						}
					}
					children = settings.toArray(new Object[0]);
				} else {
					children = new Object[0];
				}
				childrenMap.put(parentElement, children);
			}
			return children;
		}

		public Object getParent(Object element) {
			if (element instanceof EObject) {
				return ((EObject) element).eContainer();
			} else if (element instanceof FeatureValue) {
				return ((FeatureValue) element).parent;
			}
			return null;
		}

		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}
	}

	private class ColumnableLableProvider extends LabelProvider
			implements
				ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			if (columnIndex == 1) {
				return null;
			}
			return getImage(element);
		}

		public String getColumnText(Object element, int columnIndex) {
			if (columnIndex == 1) {
				if (element instanceof FeatureValue
						&& ((FeatureValue) element).isMany()) {
					return Boolean.toString(dataContext.getMasterFeatures()
							.contains(((FeatureValue) element).feature));
				}
				return "";
			}
			return getText(element);
		}
		public String getText(Object element) {
			if (element instanceof EObject) {
				EClass eClass = ((EObject) element).eClass();
				return eClass.getName();
			}
			return super.getText(element);
		}
		public Image getImage(Object element) {
			Object delagate = null;
			if (element instanceof FeatureValue) {
				delagate = ((FeatureValue) element).feature;
			} else if (element instanceof EObject) {
				delagate = ((EObject) element).eClass();
			}
			Object image = adapter.getImage(delagate);
			return ExtendedImageRegistry.getInstance().getImage(image);
		}
	}

	private class FeatureValue {
		EStructuralFeature feature;
		Object value;
		EObject parent;
		public FeatureValue(EObject parent, EStructuralFeature feature,
				Object value) {
			this.parent = parent;
			this.feature = feature;
			this.value = value;
		}

		public void setMaster(boolean isMaster) {
			if (!isMany()) {
				return;
			}
			EAnnotation eAnnotation = feature
					.getEAnnotation(XWTCodegen.EMF_FEATURE_MASTER_KEY);
			if (isMaster) {
				if (eAnnotation == null) {
					eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
					eAnnotation.setSource(XWTCodegen.EMF_FEATURE_MASTER_KEY);
					feature.getEAnnotations().add(eAnnotation);
				}
			} else if (eAnnotation != null) {
				feature.getEAnnotations().remove(eAnnotation);
			}

		}

		public boolean isMany() {
			return feature != null && feature.isMany();
		}

		public String toString() {
			String str = feature.getName();
			if (value != null) {
				String valueStr = null;
				if (feature.isMany()) {
					EClassifier eType = feature.getEType();
					valueStr = eType.getName();
				} else if (value instanceof Collection<?>) {
					Class<?> valueType = value.getClass();
					Class<?> componentType = valueType.getComponentType();
					valueStr = componentType.getSimpleName() + "[]";
				} else if (value instanceof EObject) {
					valueStr = ((EObject) value).eClass().getName();
				} else {
					Class<?> valueType = value.getClass();
					if (valueType.isPrimitive() || valueType == String.class) {
						valueStr = value.toString();
					} else {
						valueStr = valueType.getSimpleName();
					}
				}
				if (str == null) {
					str = valueStr;
				} else {
					str = str + " - " + valueStr;
				}
			} else {
				str = str + " - (unset)";
			}
			return str == null ? super.toString() : str;
		}
	}

	private class CheckBoxCellEditor extends CellEditor {

		private Button checkBox;
		public CheckBoxCellEditor(Composite parent) {
			super(parent, SWT.CENTER);
		}

		protected Control createControl(Composite parent) {
			if (checkBox == null || checkBox.isDisposed()) {
				checkBox = new Button(parent, SWT.CHECK);
			}
			return checkBox;
		}

		protected Object doGetValue() {
			if (checkBox != null && !checkBox.isDisposed()) {
				return checkBox.getSelection();
			}
			return false;
		}

		public LayoutData getLayoutData() {
			LayoutData layoutData = super.getLayoutData();
			layoutData.horizontalAlignment = SWT.CENTER;
			layoutData.grabHorizontal = false;
			return layoutData;
		}

		protected void doSetFocus() {
			if (checkBox != null && !checkBox.isDisposed()) {
				checkBox.setFocus();
			}
		}

		protected void doSetValue(Object value) {
			if (checkBox != null && !checkBox.isDisposed()) {
				boolean selection = false;
				if (value != null) {
					selection = Boolean.parseBoolean(value.toString());
				}
				checkBox.setSelection(selection);
			}
		}

	}
}
