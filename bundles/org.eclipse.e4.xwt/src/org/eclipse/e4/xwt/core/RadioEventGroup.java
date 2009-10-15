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
package org.eclipse.e4.xwt.core;

import org.eclipse.e4.xwt.IObservableValueManager;
import org.eclipse.e4.xwt.metadata.IProperty;

public class RadioEventGroup extends AbstractEventGroup {

	public RadioEventGroup(String... names) {
		super(names);
	}

	public void fireEvent(Object object, IProperty property) {

	}

	public void registerEvent(IObservableValueManager manager,
			IProperty property) {
//		for (String name : getEventNames()) {
//			String key = "is" + name + "Event";
//			if (!raisedKey.equalsIgnoreCase(key)) {
//				UserData.removeLocalData(element, key.toLowerCase());
//			}
//		}
	}

	// public void handleAfter(Object element, String event) {
	// String key = "_event.is" + event + "Event";
	// UserData.setData(element, key.toLowerCase(), true);
	// }
	//
	// public void handleBefore(Object element, String event) {
	// String raisedKey = "_event.is" + event + "Event";
	// for (String name : getEventNames()) {
	// String key = "is" + name + "Event";
	// if (!raisedKey.equalsIgnoreCase(key)) {
	// UserData.removeLocalData(element, key.toLowerCase());
	// }
	// }
	// }
}
