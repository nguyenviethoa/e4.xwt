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
import org.eclipse.e4.xwt.animation.interpolator.PointPropertyInterpolator;
import org.eclipse.e4.xwt.internal.utils.UserData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.pushingpixels.trident.TridentConfig;

/**
 * 
 * @author yyang
 */
public class PointAnimation extends AnimationTimeline {
	private Point from;
	private Point to;
	private Point by;
	private IEasingFunction easingFunction;

	static {
		TridentConfig.getInstance().addPropertyInterpolator(new PointPropertyInterpolator());
	}

	public IEasingFunction getEasingFunction() {
		return easingFunction;
	}

	public void setEasingFunction(IEasingFunction easingFunction) {
		this.easingFunction = easingFunction;
	}

	public Point getFrom() {
		return from;
	}

	public void setFrom(Point from) {
		this.from = from;
	}

	public Point getTo() {
		return to;
	}

	public void setTo(Point to) {
		this.to = to;
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
			tridentTimeline.addPropertyToInterpolate(getTargetProperty(), getFrom(), getTo());
			tridentTimeline.setEasingFunction(getEasingFunction());
		}
	}
}
