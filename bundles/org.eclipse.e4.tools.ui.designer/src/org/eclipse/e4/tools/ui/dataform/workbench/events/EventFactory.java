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
package org.eclipse.e4.tools.ui.dataform.workbench.events;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.e4.tools.ui.dataform.DataForms;
import org.eclipse.e4.tools.ui.designer.dialogs.FindElementsWithIdDialog;
import org.eclipse.e4.tools.ui.designer.utils.ApplicationModelHelper;
import org.eclipse.e4.ui.model.application.MApplicationElement;
import org.eclipse.e4.ui.model.application.impl.ApplicationPackageImpl;
import org.eclipse.e4.ui.model.application.ui.impl.UiPackageImpl;
import org.eclipse.emf.common.ui.dialogs.WorkspaceResourceDialog;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.ui.celleditor.FeatureEditorDialog;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class EventFactory {

	public static void handleEvent(Widget widget, IProject project,
			EObject parent, EObject dataContext, String featureName) {
		Shell shell = widget.getDisplay().getActiveShell();
		if (shell == null) {
			shell = new Shell();
		}
		if (parent == null) {
			parent = dataContext.eContainer();
		}
		if (dataContext == null || !(dataContext instanceof EObject)
				|| featureName == null) {
			MessageDialog.openError(shell, "Handle Event Error",
					"Handle event failed.");
			return;
		}
		EObject eObj = (EObject) dataContext;
		EClass objType = eObj.eClass();
		EStructuralFeature sf = objType.getEStructuralFeature(featureName);
		if (sf == null || sf.getEType() == null) {
			MessageDialog.openError(shell, "Handle Event Error",
					"The feature '" + featureName + "' is not found for type '"
							+ objType.getName() + "'.");
			return;
		}

		if (sf instanceof EReference) {
			handleReferences(shell, project, parent, eObj, (EReference) sf);
		} else {
			handleAttribute(shell, project, parent, eObj, (EAttribute) sf);
		}
	}

	public static void handleAttribute(Shell shell, IProject project,
			EObject parent, EObject eObj, EAttribute attribute) {
		if (eObj == null || attribute == null) {
			MessageDialog.openError(shell, "Handle Reference Error",
					"Failed to handle reference.");
			return;
		}
		String displayName = ApplicationModelHelper.getDisplayName(eObj,
				attribute);
		if (displayName == null) {
			displayName = attribute.getName();
		}
		if (attribute.isMany()) {
			FeatureEditorDialog dialog = new FeatureEditorDialog(shell,
					ApplicationModelHelper.getLabelProvider(), eObj, attribute,
					displayName, null);
			if (Window.OK == dialog.open()) {
				EList<?> result = dialog.getResult();
				eObj.eSet(attribute, result);
			}
		} else if (ApplicationPackageImpl.Literals.CONTRIBUTION__CONTRIBUTION_URI == attribute) {
			IProgressService service = PlatformUI.getWorkbench()
					.getProgressService();
			IJavaElement[] elements = new IJavaElement[] { JavaCore
					.create(project) };
			IJavaSearchScope scope = SearchEngine.createJavaSearchScope(
					elements, IJavaSearchScope.SOURCES);

			FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialog(
					shell, false, service, scope, IJavaSearchConstants.CLASS);
			dialog.create();
			dialog.setTitle("Contribution URI");
			dialog.setMessage("Choose a type for contribution.");
			dialog.getShell().setText("Contribution Dialog");
			if (dialog.open() == Window.OK) {
				IType type = (IType) dialog.getFirstResult();
				String contributionURI = createContributionURI(type);
				if (contributionURI != null) {
					eObj.eSet(attribute, contributionURI);
				}
			}
		} else if (UiPackageImpl.Literals.UI_LABEL__ICON_URI == attribute) {
			List<ViewerFilter> filters = new ArrayList<ViewerFilter>();
			ViewerFilter imageFilter = new ViewerFilter() {
				public boolean select(Viewer viewer, Object parentElement,
						Object element) {
					if (element instanceof IFile) {
						IFile file = (IFile) element;
						InputStream in = null;
						Image image = null;
						try {
							in = file.getContents();
							image = new Image(null, in);
							return ImageDescriptor.createFromImage(image) != null;
						} catch (Exception e) {
							return false;
						} finally {
							if (in != null) {
								try {
									in.close();
								} catch (IOException e) {
								}
							}
							if (image != null) {
								image.dispose();
							}
						}
					}
					return true;
				}
			};
			filters.add(imageFilter);
			IFile[] files = WorkspaceResourceDialog.openFileSelection(shell,
					"Icon URI - Attribute", "Choose Icon.", false, null,
					filters);
			if (files.length == 1) {
				String iconURI = createIconURI(files[0]);
				if (iconURI != null) {
					eObj.eSet(attribute, iconURI);
				}
			}
		} else if (DataForms.isRefSF(attribute)) {
			Object[] children = ApplicationModelHelper.getChildren(parent
					.eResource(), new IFilter() {
				public boolean select(Object toTest) {
					if (toTest instanceof EObject
							&& toTest instanceof MApplicationElement) {
						return ((EObject) toTest).eResource() != null;
					}
					return false;
				}
			}, true);
			FindElementsWithIdDialog dialog = new FindElementsWithIdDialog(
					shell, children);
			if (Window.OK == dialog.open()) {
				Object result = dialog.getFirstResult();
				eObj.eSet(attribute, result);
			}
		}
	}

	private static String createIconURI(IFile file) {
		if (file == null || !file.exists()) {
			return null;
		}
		String elementName = file.getProjectRelativePath().toString();
		String projectName = file.getProject().getName();
		return URI.createPlatformPluginURI(projectName + "/" + elementName,
				true).toString();
	}

	private static String createContributionURI(IType type) {
		if (type == null) {
			return null;
		}
		String elementName = type.getFullyQualifiedName();
		String projectName = type.getJavaProject().getElementName();
		return URI.createPlatformPluginURI(projectName + "/" + elementName,
				true).toString();
	}

	public static void handleReferences(Shell shell, IProject project,
			EObject parent, EObject eObj, EReference reference) {
		if (parent == null || eObj == null || reference == null) {
			MessageDialog.openError(shell, "Handle Reference Error",
					"Failed to handle reference.");
			return;
		}
		final EClassifier featureType = reference.getEType();
		Object[] children = ApplicationModelHelper.getChildren(parent
				.eResource(), new IFilter() {
			public boolean select(Object toTest) {
				if (!(toTest instanceof EObject)) {
					return false;
				}
				EObject eObj = (EObject) toTest;
				EClass targetType = (EClass) ((toTest instanceof EClass) ? eObj
						: eObj.eClass());
				if ((featureType == targetType || featureType instanceof EClass
						&& ((EClass) featureType).isSuperTypeOf(targetType))) {
					return true;
				}
				return false;
			}
		}, true);
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				shell, ApplicationModelHelper.getLabelProvider());
		dialog.setElements(children);
		dialog.setMultipleSelection(reference.isMany());
		Object oldValue = null;
		try {
			oldValue = eObj.eGet(reference);
		} catch (Exception e) {
		}
		if (oldValue != null) {
			dialog.setInitialElementSelections((List) (reference.isMany() ? oldValue
					: Collections.singletonList(oldValue)));
		}
		dialog.create();
		dialog.setTitle("Choose " + featureType.getName());
		dialog.getShell().setText(reference.getName() + " - Reference");
		if (dialog.open() == Window.OK) {
			Object[] result = dialog.getResult();
			if (reference.isMany()) {
				eObj.eSet(reference, Arrays.asList(result));
			} else {
				eObj.eSet(reference, result[0]);
			}
		}
	}
}
