/*******************************************************************************
 * Copyright (c) 2008, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Soyatec - porting on XWT 
 *******************************************************************************/
package org.eclipse.e4.demo.e4photo;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.services.Logger;
import org.eclipse.e4.core.services.annotations.In;
import org.eclipse.e4.core.services.annotations.Out;
import org.eclipse.e4.demo.e4photo.xwt.EditDialog;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.ui.workbench.views.XWTStaticPart;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Event;

public class ExifTable extends XWTStaticPart {
	private WritableList inputList = new WritableList();
	private IContainer input;
	private Exif selection;

	private Logger logger;

	public ExifTable() {
		super();
	}

	@In
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Out
	public Exif getSelection() {
		return selection;
	}

	@In
	public void setInput(Object input) {
		if (selection == null && !(input instanceof IResource))
			return;
		IResource selection = (IResource) input;
		IContainer newInput;
		if (selection instanceof IContainer)
			newInput = (IContainer) selection;
		else
			newInput = selection.getParent();
		if (newInput == this.input)
			return;
		this.input = newInput;

		inputList.clear();
		try {
			IResource[] members = this.input.members();
			for (int i = 0; i < members.length; i++) {
				IResource resource = members[i];
				if (resource.getType() == IResource.FILE) {
					InputStream contents = ((IFile) resource).getContents();
					try {
						Exif exif = new Exif(resource.getLocationURI(), contents);
						inputList.add(exif);
					} catch (Exception e) {
						logger.warn(((IFile) resource).getFullPath() + ": " + e.getMessage());
					} finally {
						try {
							contents.close();
						} catch (IOException e) {
							logger.warn(e, "Could not close stream");
						}
					}
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public Class<?> getInputType() {
		return IResource.class;
	}

	@Override
	public Object getDataContext() {
		return inputList;
	}
	
	public void showLocation(Event event) {
		Viewer viewer = (Viewer) XWT.findElementByName(event.widget, "exifTable");
		ISelection selection = viewer.getSelection();
		if (selection.isEmpty()) {
			return;
		}
		Object target = ((IStructuredSelection) selection).getFirstElement();
		getContext().set(IServiceConstants.SELECTION, target);
	}

	public void editExif(Event event) {
		Viewer viewer = (Viewer) XWT.findElementByName(event.widget, "exifTable");
		ISelection selection = viewer.getSelection();
		if (selection.isEmpty()) {
			return;
		}
		Object target = ((IStructuredSelection) selection).getFirstElement();
		Exif target2 = (Exif) target;

		EditDialog editDialog = new EditDialog(event.widget.getDisplay().getActiveShell(), "Exif edition", target2);
		if (editDialog.open() == Dialog.OK) {
			viewer.refresh();
		}
	}
}
