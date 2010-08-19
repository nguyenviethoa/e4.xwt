/*******************************************************************************
 * Copyright (c) 2010 Angelo Zerr and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *******************************************************************************/
package ui;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;

import services.IHelloService;

/**
 * Event handler used by "ui.xwt" file when button is clicked. This CLR is created
 * by Spring (see Spring bean declaration from "META-INF/spring/ui-context.xml"). The CLR use
 * {@link IHelloService} filled with Spring DI.
 * 
 */
public class EventHandler {

	private IHelloService helloService;

	public void setHelloService(IHelloService helloService) {
		this.helloService = helloService;
	}

	protected void clickButton(Event event) {
		Button button = (Button) event.widget;
		// Use the hello service
		button.setText(helloService.hello());
	}
}