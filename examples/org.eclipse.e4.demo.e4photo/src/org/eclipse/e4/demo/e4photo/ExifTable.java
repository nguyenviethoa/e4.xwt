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

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.services.Logger;
import org.eclipse.e4.core.services.annotations.Optional;
import org.eclipse.e4.demo.e4photo.xwt.EditDialog;
import org.eclipse.e4.ui.services.events.IEventBroker;
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
	private String persistedState;

	static public String EVENT_NAME = "org/eclipse/e4/demo/e4photo/exif"; 

	@Inject
	private Logger logger;

	@Inject
	private IEventBroker eventBroker;

	public ExifTable() {
		super();
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Inject @Optional
	void setSelection(@Named("selection") IResource selection) {
		if (selection == null)
			return;
		IContainer newInput;
		if (selection instanceof IContainer)
			newInput = (IContainer) selection;
		else
			newInput = selection.getParent();
		if (newInput == input)
			return;
		input = newInput;

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

	@Inject @Optional
	void setPersistedState(@Named("persistedState") String persistedState) {
		firePropertyChange(new PropertyChangeEvent(this, "persistedState", 
				this.persistedState, this.persistedState = persistedState));
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
		Object selected = ((IStructuredSelection) selection).getFirstElement();
		if (eventBroker != null)
			eventBroker.post(EVENT_NAME, selected);
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
