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
package org.eclipse.e4.xwt.animation;

import org.eclipse.e4.xwt.annotation.Containment;

public class TimelineGroup extends Timeline {
	private Timeline[] children = EMPTY_ARRAY;

	@Containment
	public Timeline[] getChildren() {
		return children;
	}

	public void setChildren(Timeline[] children) {
		this.children = children;
	}
	
	public void start(Object target) {
		for (Timeline timeline : getChildren()) {
			timeline.start(target);
		}
	}

	@Override
	public void stop() {
		for (Timeline timeline : getChildren()) {
			timeline.stop();
		}
	}
	
	@Override
	public void pause() {
		for (Timeline timeline : getChildren()) {
			timeline.pause();
		}
	}
	
	@Override
	public void resume() {
		for (Timeline timeline : getChildren()) {
			timeline.resume();
		}
	}
	
	

	@Override
	protected void doStart(org.pushingpixels.trident.Timeline timeline,
			Object target) {
	}
}
