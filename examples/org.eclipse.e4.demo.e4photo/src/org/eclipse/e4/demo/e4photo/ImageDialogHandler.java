/*******************************************************************************
 * Copyright (c) 2008, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.demo.e4photo;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.e4.core.services.context.EclipseContextFactory;
import org.eclipse.e4.core.services.context.IEclipseContext;
import org.eclipse.e4.core.services.context.spi.ContextFunction;
import org.eclipse.e4.ui.model.application.ApplicationFactory;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.MContributedPart;
import org.eclipse.e4.ui.model.application.MPart;
import org.eclipse.e4.ui.model.application.MSashForm;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.workbench.ui.internal.UISchedulerStrategy;
import org.eclipse.e4.workbench.ui.internal.Workbench;
import org.eclipse.e4.workbench.ui.renderers.PartRenderingEngine;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class ImageDialogHandler {

	private class ImageDialog extends Dialog {
		private PartRenderingEngine theRenderer;
		private IEclipseContext dlgContext;

		public ImageDialog(Shell shell, MApplication<?> app, PartRenderingEngine renderer) {
			super(shell);

			theRenderer = renderer;
			
			dlgContext = EclipseContextFactory
			.create(app.getContext(), UISchedulerStrategy.getInstance());

			// 'adopt' the app's 'INPUT' (used to support selection, we should 
			// replace this with a viable strategy to support 'local' selection
			dlgContext.set(IServiceConstants.INPUT, new ContextFunction() {
				public Object compute(IEclipseContext context, Object[] arguments) {
					Class<?> adapterType = null;
					if (arguments.length > 0 && arguments[0] instanceof Class<?>) {
						adapterType = (Class<?>) arguments[0];
					}
					Object newInput = null;
					Object newValue = dlgContext.get(IServiceConstants.SELECTION);
					if (adapterType == null || adapterType.isInstance(newValue)) {
						newInput = newValue;
					} else if (newValue != null && adapterType != null) {
						IAdapterManager adapters = (IAdapterManager) dlgContext
								.get(IAdapterManager.class.getName());
						if (adapters != null) {
							Object adapted = adapters.loadAdapter(newValue, adapterType.getName());
							if (adapted != null) {
								newInput = adapted;
							}
						}
					}
					return newInput;
				}
			});
		}
		
		protected Control createDialogArea(Composite parent) {
			// Composite boilerplate
			Composite comp = new Composite(parent, SWT.NULL);
			comp.setLayout(new FillLayout());
			comp.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			// Create the model and use it to fill in the composite
			MPart<MPart<?>> dlgModel = createDlgModel();
			Workbench.initializeContext(dlgContext, dlgModel);
			theRenderer.createGui(dlgModel, comp);

			// Declare the source for selection listeners
			dlgContext.set(IServiceConstants.ACTIVE_CHILD, dlgModel.getContext());
			
			// Can't link to the app because doing so causes the actual app to
			// see the selection events from the dialog
			//theApp.getContext().set(IServiceConstants.ACTIVE_CHILD, dlgContext);
			
			return comp;
		}
		
		private MPart<MPart<?>> createDlgModel() {
			// Create a side-by-side sash
			MSashForm<MPart<?>> sash = ApplicationFactory.eINSTANCE.createMSashForm();
			sash.setPolicy("Horizontal");
			
			// Create the 'Library' part
			MContributedPart<?> library = ApplicationFactory.eINSTANCE.createMContributedPart();
			library.setURI("platform:/plugin/org.eclipse.e4.demo.e4photo/org.eclipse.e4.demo.e4photo.Library");
			library.setName("Library");
			
			// Create the 'Preview' part
			MContributedPart<?> preview = ApplicationFactory.eINSTANCE.createMContributedPart();
			preview.setURI("platform:/plugin/org.eclipse.e4.demo.e4photo/org.eclipse.e4.demo.e4photo.Preview");
			preview.setName("Preview");
			
			// Add them to the sash, library first
			sash.getChildren().add(library);
			sash.getChildren().add(preview);
			
			return sash;
		}

		// Dialog boilerplate
		protected void configureShell(Shell shell) {
			super.configureShell(shell);
			shell.setText("Image Dialog");
			shell.setSize(600, 400);
		}
		public void create() {
			super.create();
			getButton(IDialogConstants.OK_ID).setEnabled(true);
		}
		protected boolean isResizable() {
			return true;
		}
	}
	
	public void execute(Shell shell, MApplication<?> app, PartRenderingEngine renderer) {
		ImageDialog dlg = new ImageDialog(shell, app, renderer);
		dlg.open();
	}

}
