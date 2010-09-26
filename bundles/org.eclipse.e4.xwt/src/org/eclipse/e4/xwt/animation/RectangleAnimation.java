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

import org.eclipse.e4.xwt.XWTException;
import org.eclipse.e4.xwt.animation.internal.ITimeline;
import org.eclipse.e4.xwt.animation.internal.TridentTimeline;
import org.eclipse.e4.xwt.animation.interpolator.RectanglePropertyInterpolator;
import org.eclipse.e4.xwt.internal.utils.UserData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.pushingpixels.trident.TridentConfig;

/**
 * 
 * @author yyang
 */
public class RectangleAnimation extends AnimationTimeline {
	private Rectangle from;
	private Rectangle to;
	private Rectangle by;
	
	private IEasingFunction easingFunction;

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
	
	public IEasingFunction getEasingFunction() {
		return easingFunction;
	}

	public void setEasingFunction(IEasingFunction easingFunction) {
		this.easingFunction = easingFunction;
	}

	protected void initialize(Object target) {
		if (getFrom() == null && getTo() == null) {
			super.initializeCacheValue(target);
		}
	}

	protected void updateTimeline(ITimeline timeline, Object target) {
		super.updateTimeline(timeline, target);
		Object widget = UserData.getWidget(target);
		if (!(widget instanceof Control)) {
			throw new XWTException(
					"The target of the animation is not a Control.");
		}
		if (timeline instanceof TridentTimeline) {
			TridentTimeline tridentTimeline = (TridentTimeline) (timeline);
			Rectangle from = getFrom();
			Rectangle to = getTo();
			if (from == null && to == null) {
				from = (Rectangle) getCacheValue();
				to = (Rectangle) getCurrentValue(target);
				if (from.width == 0 && from.height == 0) {
					setCacheValue(to);
					return;
				}
				if (from != null && from.equals(to)) {
					return;
				}
			}
			tridentTimeline.addPropertyToInterpolate(getTargetProperty(), from, to);
			tridentTimeline.setEasingFunction(getEasingFunction());
		}
	}
}
