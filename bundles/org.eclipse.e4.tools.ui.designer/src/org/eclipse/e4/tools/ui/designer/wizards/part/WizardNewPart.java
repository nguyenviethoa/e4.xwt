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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.xwt.ui.utils.ProjectContext;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.actions.WorkbenchRunnableAdapter;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * @author Jin Liu(jin.liu@soyatec.com)
 */
public abstract class WizardNewPart extends NewElementWizard {

	protected IFile fFile;
	protected MPart fPart;

	private ProjectContext fProjectContext;

	private NewClassWizardPage newTypeWizardPage;

	public WizardNewPart(IFile file, MPart part) {
		this.fFile = file;
		this.fPart = part;
		fProjectContext = ProjectContext.getContext(JavaCore.create(file
				.getProject()));
		setDefaultPageImageDescriptor(JavaPluginImages.DESC_WIZBAN_NEWCLASS);
		setDialogSettings(JavaPlugin.getDefault().getDialogSettings());
		setWindowTitle("New Part");
	}

	public boolean performFinish() {
		boolean performFinish = super.performFinish();
		if (performFinish && getCreatedElement() != null) {
			final IType type = (IType) getCreatedElement();
			IWorkspaceRunnable op = new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException,
						OperationCanceledException {
					refreshLoadClass(type, monitor);
				}
			};
			try {
				ISchedulingRule rule = null;
				Job job = Job.getJobManager().currentJob();
				if (job != null)
					rule = job.getRule();
				IRunnableWithProgress runnable = null;
				if (rule != null)
					runnable = new WorkbenchRunnableAdapter(op, rule, true);
				else
					runnable = new WorkbenchRunnableAdapter(op,
							getSchedulingRule());
				getContainer().run(canRunForked(), true, runnable);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String elementName = type.getFullyQualifiedName();
			String projectName = type.getJavaProject().getElementName();
			String partURI = URI.createPlatformPluginURI(
					projectName + "/" + elementName, true).toString();
			fPart.setContributionURI(partURI);
			fPart.setLabel(type.getElementName());
		}
		return performFinish;
	}

	// Try to load the new created Type here, so that the
	// BundleClassLoader can find the class easily.
	private void refreshLoadClass(IType type, IProgressMonitor monitor) {
		try {
			Class<?> loadClass = fProjectContext.loadClass(type
					.getFullyQualifiedName());
			while (loadClass == null) {
				try {
					type.getJavaProject().getProject().build(
							IncrementalProjectBuilder.INCREMENTAL_BUILD,
							new SubProgressMonitor(monitor, 10));
					loadClass = fProjectContext.loadClass(type
							.getFullyQualifiedName());
				} catch (CoreException e1) {
				}
			}
		} catch (Exception e) {
		}
	}

	public void setNewTypeWizardPage(NewClassWizardPage newTypeWizardPage) {
		this.newTypeWizardPage = newTypeWizardPage;
	}

	protected boolean canRunForked() {
		if (newTypeWizardPage != null) {
			return !newTypeWizardPage.isEnclosingTypeSelected();
		}
		return false;
	}

	protected void finishPage(IProgressMonitor monitor)
			throws InterruptedException, CoreException {
		if (newTypeWizardPage != null) {
			newTypeWizardPage.createType(monitor);
		}
	}

	public IJavaElement getCreatedElement() {
		if (newTypeWizardPage != null) {
			return newTypeWizardPage.getCreatedType();
		}
		return null;
	}

}
