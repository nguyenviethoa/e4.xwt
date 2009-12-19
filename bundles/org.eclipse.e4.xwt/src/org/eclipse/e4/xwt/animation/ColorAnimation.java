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
package org.eclipse.e4.xwt.animation;

import org.eclipse.swt.graphics.Color;
import org.pushingpixels.trident.Timeline;

public class ColorAnimation extends AnimationTimeline {
	private Color from;
	private Color by;
	private Color to;
	
	public Color getTo() {
		return to;
	}
	
	public void setTo(Color to) {
		this.to = to;
	}
	
	public Color getFrom() {
		return from;
	}
	
	public void setFrom(Color from) {
		this.from = from;
	}
	
	public Color getBy() {
		return by;
	}
	
	public void setBy(Color by) {
		this.by = by;
	}
	
	@Override
	protected void doStart(Timeline timeline, Object target) {
		timeline.addPropertyToInterpolate(getTargetProperty(), getFrom(), getTo());
	}
}
