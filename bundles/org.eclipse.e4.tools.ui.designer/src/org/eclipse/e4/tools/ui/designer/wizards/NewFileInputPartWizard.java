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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public class NewFileInputPartWizard extends WizardNewPart {

	private NewFileInputSelectionWizardPage fSelectionPage;
	private NewFileInputPartWizardPage fPartPage;

	private DataContext dataContext;
	private MApplication application;
	public NewFileInputPartWizard(IFile file, MPart part,
			MApplication application) {
		super(file, part);
		this.application = application;
	}

	public void addPages() {
		dataContext = new DataContext();
		fSelectionPage = new NewFileInputSelectionWizardPage(dataContext);
		addPage(fSelectionPage);

		fPartPage = new NewFileInputPartWizardPage(dataContext);
		fPartPage.init(new StructuredSelection(fFile));
		addPage(fPartPage);
	}

	protected void finishPage(IProgressMonitor monitor)
			throws InterruptedException, CoreException {
		fPartPage.createType(monitor);
	}

	public IJavaElement getCreatedElement() {
		return fPartPage.getCreatedType();
	}

	public boolean performFinish() {
		boolean performFinish = super.performFinish();
		if (performFinish && application != null
				&& !dataContext.getMasterFeatures().isEmpty()) {
			// try to add this variable, so that, the selection changed event
			// will dispatch to all sub contexts, otherwise not.
			List<MWindow> children = application.getChildren();
			if (children.isEmpty()) {
				application.getVariables().add(IServiceConstants.SELECTION);
			} else {
				for (MWindow mWindow : children) {
					mWindow.getVariables().add(IServiceConstants.SELECTION);
				}
			}
		}
		return performFinish;
	}

	public static class DataContext {

		private EObject eObject;
		private List<EStructuralFeature> features;
		private List<EStructuralFeature> masterFeatures;
		private IFile input;

		private PropertyChangeSupport support = new PropertyChangeSupport(this);

		public void setEObject(EObject eObject) {
			EObject oldValue = this.eObject;
			this.eObject = eObject;
			support.firePropertyChange("EObject", oldValue, this.eObject);
		}

		public EObject getEObject() {
			return eObject;
		}

		public List<EStructuralFeature> getFeatures() {
			if (features == null) {
				features = new ArrayList<EStructuralFeature>();
			}
			return features;
		}

		public List<EStructuralFeature> getMasterFeatures() {
			if (masterFeatures == null) {
				masterFeatures = new ArrayList<EStructuralFeature>();
			}
			return masterFeatures;
		}

		public void setInput(IFile input) {
			IFile oldValue = this.input;
			this.input = input;
			support.firePropertyChange("FILE", oldValue, this.input);
		}

		public IFile getInput() {
			return input;
		}

		public void clear() {
			eObject = null;
			getFeatures().clear();
			input = null;
		}

		public void addPropertyChangeListener(PropertyChangeListener listener) {
			support.addPropertyChangeListener(listener);
		}

		public void removePropertyChangeListener(PropertyChangeListener listener) {
			support.removePropertyChangeListener(listener);
		}
	}

}
