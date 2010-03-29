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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.e4.xwt.ui.utils.ProjectContext;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
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
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class NewSelectionPartDataContextPage extends WizardPage {

	private SingleCheckBoxTreeViewer dataContextTypeViewer;
	private NewSelectionPartWizardPage fTypePage;
	private Text sourceText;
	private boolean displaySuperTypes = true;
	private boolean displayComplexTypes = false;

	protected NewSelectionPartDataContextPage(
			NewSelectionPartWizardPage typePage) {
		super("DataContextSelectionPage");
		this.fTypePage = typePage;
		setTitle("Data Context Selection");
		setMessage("Choose a Model as data context to create part.");
	}
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		Composite control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout(3, false));

		Label sourceLabel = new Label(control, SWT.NONE);
		sourceLabel.setText("Source");

		sourceText = new Text(control, SWT.BORDER);
		sourceText.setLayoutData(GridDataFactory.fillDefaults().grab(true,
				false).create());

		Button browserButton = new Button(control, SWT.PUSH);
		browserButton.setText("Browser...");
		browserButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				chooseDataContext();
			}
		});

		dataContextTypeViewer = new SingleCheckBoxTreeViewer(control,
				SWT.BORDER);
		dataContextTypeViewer.getTree().setLayoutData(
				GridDataFactory.fillDefaults().span(3, 1).grab(true, true)
						.create());
		dataContextTypeViewer.setContentProvider(new ContentProvider());
		dataContextTypeViewer.setLabelProvider(new LabelProviderEx());
		dataContextTypeViewer.setInput(JavaCore.create(ResourcesPlugin
				.getWorkspace().getRoot()));
		dataContextTypeViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				handleSelection();
			}
		});

		final Button hideSuperTypesButton = new Button(control, SWT.CHECK);
		hideSuperTypesButton.setText("Hide properties of super types");
		hideSuperTypesButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				displaySuperTypes = !hideSuperTypesButton.getSelection();
				dataContextTypeViewer.refresh();
			}
		});
		hideSuperTypesButton.setSelection(!displaySuperTypes);
		hideSuperTypesButton.setLayoutData(GridDataFactory.fillDefaults().span(
				3, 1).create());

		final Button hideComplexTypesButton = new Button(control, SWT.CHECK);
		hideComplexTypesButton.setText("Hide complex properties of types");
		hideComplexTypesButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				displayComplexTypes = !hideComplexTypesButton.getSelection();
				dataContextTypeViewer.refresh();
			}
		});
		hideComplexTypesButton.setSelection(!displayComplexTypes);
		hideComplexTypesButton.setLayoutData(GridDataFactory.fillDefaults()
				.span(3, 1).create());

		setControl(control);
		Dialog.applyDialogFont(control);
	}

	protected void chooseDataContext() {
		ResourceListSelectionDialog dialog = new ResourceListSelectionDialog(
				getShell(), ResourcesPlugin.getWorkspace().getRoot(),
				IResource.FILE) {
			private boolean isCreating;
			protected Control createDialogArea(Composite parent) {
				Control control = super.createDialogArea(parent);
				isCreating = true;
				refresh(true);
				isCreating = false;
				return control;
			}
			protected String adjustPattern() {
				String adjustPattern = super.adjustPattern();
				if (isCreating && "".equals(adjustPattern)) {
					return "*.java";
				}
				return adjustPattern;
			}
		};
		dialog.setTitle("Data Context Selection Dialog");
		dialog.setMessage("Select a *.java or EMF model file to retrieve data context.");
		if (Window.OK == dialog.open()) {
			Object[] result = dialog.getResult();
			if (result.length >= 1) {
				IFile file = (IFile) result[0];
				dataContextTypeViewer.setInput(computeInput(file));
				sourceText.setText(file.getFullPath().toString());
			} else {
				sourceText.setText("");
			}
		}
	}

	protected Object computeInput(IFile file) {
		String fileExtension = file.getFileExtension();
		if ("java".equals(fileExtension)) {
			ICompilationUnit javaElement = (ICompilationUnit) JavaCore
					.create(file);
			return javaElement;
		} else {
			ResourceSet rs = new ResourceSetImpl();
			Resource resource = null;
			try {
				URI uri = URI.createPlatformPluginURI(file.getFullPath()
						.toString(), true);
				resource = rs.getResource(uri, true);
			} catch (Exception e) {
				try {
					URI uri = URI.createPlatformResourceURI(file.getFullPath()
							.toString(), true);
					resource = rs.getResource(uri, true);
				} catch (Exception e1) {
					try {
						URI uri = URI.createFileURI(file.getLocation()
								.toString());
						resource = rs.getResource(uri, true);
					} catch (Exception e2) {
					}
				}
			}
			return resource;
		}
	}
	protected void handleSelection() {
		Object checked = dataContextTypeViewer.getChecked();
		if (checked == null) {
			setErrorMessage("Selection Type is empty.");
		} else {
			setErrorMessage(null);
			fTypePage.setDataContext(convertDataContext(checked));
			List<Object> checkedChildren = dataContextTypeViewer
					.getCheckedChildren();
			List<String> dataProperties = new ArrayList<String>();
			for (Object object : checkedChildren) {
				if (object instanceof PropertyDescriptor) {
					dataProperties.add(((PropertyDescriptor) object).getName());
				} else if (object instanceof EStructuralFeature) {
					dataProperties.add(((EStructuralFeature) object).getName());
				}
			}
			fTypePage.setDataContextProperties(dataProperties);
		}
	}

	private Object convertDataContext(Object object) {
		if (object == null) {
			return null;
		}
		if (object instanceof IType) {
			return getBeanType((IType) object);
		}
		return object;
	}

	private Class<?> getBeanType(IType type) {
		try {
			IJavaProject javaProject = type.getJavaProject();
			return ProjectContext.getContext(javaProject).loadClass(
					type.getFullyQualifiedName());
		} catch (Exception e) {
		}
		return null;
	}

	private class LabelProviderEx extends LabelProvider {
		public String getText(Object element) {
			if (element instanceof IJavaElement) {
				return ((IJavaElement) element).getElementName();
			} else if (element instanceof PropertyDescriptor) {
				String displayName = ((PropertyDescriptor) element).getName();
				Class<?> propertyType = ((PropertyDescriptor) element)
						.getPropertyType();
				if (propertyType != null) {
					String typeName = propertyType.getSimpleName();
					return displayName + " - " + typeName;
				}
				return displayName;
			} else if (element instanceof ETypedElement) {
				String typeName = ((ETypedElement) element).getEType()
						.getName();
				String name = ((ETypedElement) element).getName();
				return name + " - " + typeName;
			} else if (element instanceof ENamedElement) {
				return ((ENamedElement) element).getName();
			} else if (element instanceof Class<?>) {
				return ((Class<?>) element).getSimpleName();
			}
			return super.getText(element);
		}
		public Image getImage(Object element) {
			if (element instanceof PropertyDescriptor
					|| element instanceof EStructuralFeature) {
				return JavaPluginImages.get(JavaPluginImages.IMG_FIELD_PUBLIC);
			} else {
				return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_CLASS);
			}
		}
	}

	private class ContentProvider implements ITreeContentProvider {

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof ICompilationUnit) {
				IType type = ((ICompilationUnit) inputElement)
						.findPrimaryType();
				if (type != null) {
					return new Object[]{type};
				}
			} else if (inputElement instanceof Resource) {
				EList<EObject> contents = ((Resource) inputElement)
						.getContents();
				EPackage ePackage = null;
				for (EObject eObject : contents) {
					if (eObject instanceof EPackage) {
						ePackage = (EPackage) eObject;
					} else if (eObject instanceof EClass) {
						ePackage = ((EClass) eObject).getEPackage();
					} else {
						EClass eClass = eObject.eClass();
						ePackage = eClass.getEPackage();
					}
					if (ePackage != null) {
						break;
					}
				}
				return getChildren(ePackage);
			}
			return new Object[0];
		}

		public void dispose() {

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof IType) {
				IType type = (IType) parentElement;
				Class<?> clazz = getBeanType(type);
				if (clazz != null) {
					return getChildren(clazz);
				}
			} else if (parentElement instanceof Class<?>) {
				try {
					Class<?> clazz = (Class<?>) parentElement;
					Class<?> superclass = clazz.getSuperclass();
					BeanInfo beanInfo = java.beans.Introspector.getBeanInfo(
							clazz, superclass);
					PropertyDescriptor[] properties = beanInfo
							.getPropertyDescriptors();
					List<Object> children = new ArrayList<Object>();
					for (PropertyDescriptor pd : properties) {
						Class<?> propertyType = pd.getPropertyType();
						if (propertyType == null) {
							continue;
						}
						if (!displayComplexTypes
								&& !(propertyType.isPrimitive()
										|| (propertyType == String.class) || propertyType
										.isEnum())) {
							continue;
						}
						children.add(pd);
					}
					if (displaySuperTypes && Object.class != superclass) {
						children.add(superclass);
					}
					return children.toArray(new Object[0]);
				} catch (IntrospectionException e) {
				}
			} else if (parentElement instanceof EPackage) {
				return ((EPackage) parentElement).getEClassifiers().toArray(
						new Object[0]);
			} else if (parentElement instanceof EClass) {
				EClass eClass = (EClass) parentElement;
				List<Object> objects = new ArrayList<Object>();
				EList<EStructuralFeature> allFeatures = eClass
						.getEStructuralFeatures();
				for (EStructuralFeature sf : allFeatures) {
					if (!displayComplexTypes
							&& !(sf.getEType() instanceof EDataType)) {
						continue;
					}
					objects.add(sf);
				}
				if (displaySuperTypes) {
					objects.addAll(eClass.getESuperTypes());
				}
				return objects.toArray(new Object[0]);
			}
			return new Object[0];
		}

		public Object getParent(Object element) {
			if (element instanceof EObject) {
				return ((EObject) element).eContainer();
			}
			return null;
		}

		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}

	}
}
