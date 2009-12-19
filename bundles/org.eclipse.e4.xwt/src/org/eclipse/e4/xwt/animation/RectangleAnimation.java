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

import org.eclipse.e4.xwt.XWTException;
import org.eclipse.e4.xwt.animation.interpolator.RectanglePropertyInterpolator;
import org.eclipse.e4.xwt.internal.utils.UserData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.TridentConfig;

public class RectangleAnimation extends AnimationTimeline {
	private Rectangle from;
	private Rectangle to;
	private Rectangle by;
	
	static {
		TridentConfig.getInstance().addPropertyInterpolator(new RectanglePropertyInterpolator());
	}
	
	public Rectangle getFrom() {
		return from;
	}

	public void setFrom(Rectangle from) {
		this.from = from;
	}

	public Rectangle getTo() {
		return to;
	}

	public void setTo(Rectangle to) {
		this.to = to;
	}

	@Override
	protected void doStart(Timeline timeline, Object target) {
		Object widget = UserData.getWidget(target);
		if (!(widget instanceof Control)) {
			throw new XWTException(
					"The target of the animation is not a Control.");
		}
		timeline.addPropertyToInterpolate(getTargetProperty(), getFrom(), getTo());
	}
}
