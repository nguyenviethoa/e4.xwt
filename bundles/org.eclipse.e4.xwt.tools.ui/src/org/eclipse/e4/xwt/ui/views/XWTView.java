/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.ui.views;

import java.io.File;
import java.io.FileWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.e4.xwt.ILoadingContext;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.ui.XWTUIPlugin;
import org.eclipse.e4.xwt.ui.utils.ProjectContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.ViewPart;

public class XWTView extends ViewPart {

	public static final String ID = "org.eclipse.e4.xwt.ui.views.XWTView";

	protected Composite container;
	private ProjectContext projectContext;

	/**
	 * The constructor.
	 */
	public XWTView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		container.setLayout(new GridLayout());
		container.setBackground(container.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		container.setBackgroundMode(SWT.INHERIT_DEFAULT);
	}

	public void setContent(String code, IFile file) {
		setContent(code, new ProjectContentProvider(file));
	}

	public void setContent(String code, IContentProvider contentProvider) {
		try {
			setContentWithException(code, contentProvider);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setContentWithException(String code, IContentProvider contentProvider) throws Exception {
		XWTUIPlugin.checkStartup();
		for (Control child : container.getChildren()) {
			child.dispose();
		}
		ILoadingContext loadingContext = contentProvider.getLoadingContext();
		if (loadingContext != null) {
			XWT.setLoadingContext(loadingContext);
		}
		if (code != null) {
			File file = new File("c:/text.xwt");
			try {
				FileWriter fileWriter = new FileWriter(file);
				fileWriter.write(code);
				fileWriter.close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			XWT.load(container, file.toURI().toURL());
		} else {
			XWT.load(container, contentProvider.getContentURL());
		}
		container.layout(true, true);
	}

	public void setContentWithException(String code, IFile file) throws Exception {
		setContentWithException(code, new ProjectContentProvider(file));
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}
}
