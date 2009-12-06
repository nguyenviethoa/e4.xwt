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

import javax.inject.Inject;

import org.eclipse.e4.ui.services.events.IEventBroker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class Location implements EventHandler {

	private Browser browser;
	private Composite browserParent;
	private Exif exif;

	@Inject
	public Location(Composite parent, IEventBroker eventBroker) {
		parent.setLayout(new FillLayout());
		parent.setData("org.eclipse.e4.ui.css.id", "location");
		browserParent = parent;
		eventBroker.subscribe(ExifTable.EVENT_NAME, this);
	}

	public void handleEvent(Event event) {
		Exif input = (Exif) event.getProperty(IEventBroker.DATA);
		if (input == null || this.exif == input) {
			return;
		}
		this.exif = (Exif) input;

		// Create Browser widget only when we have content to show
		// so that we can control background color when there is no content
		if (exif == null || exif.getGpsLatitude() == null) {
			if (browser != null) {
				browser.dispose();
				browser = null;
			}
		} else {
			if (browser == null) {
				browser = new Browser(browserParent, SWT.NONE);
				browserParent.layout();
			}
			browser.setUrl("http://maps.google.com/maps?q="
					+ exif.getGpsLatitude() + "+" + exif.getGpsLongitude());
		}
	}

}
