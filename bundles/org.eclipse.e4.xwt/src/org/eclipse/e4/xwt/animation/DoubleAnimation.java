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

import org.pushingpixels.trident.Timeline;

public class DoubleAnimation extends DoubleAnimationBase {
	protected double by;
	protected double from;
	protected double to;
	
	protected boolean isAdditive;
	protected boolean isCumulativeProperty;	

	public DoubleAnimation(double toValue, Duration duration) {
		setTo(toValue);
		setDuration(duration);
	}
	
	public DoubleAnimation(double fromValue, double toValue, Duration duration) {
		setTo(toValue);
		setFrom(fromValue);
		setDuration(duration);
	}
	
	public DoubleAnimation(double toValue, Duration duration, FillBehavior fillBehavior) {
		setTo(toValue);
		setDuration(duration);
		setFillBehavior(fillBehavior);
	}
	
	public DoubleAnimation(double fromValue, double toValue, Duration duration, FillBehavior fillBehavior) {
		setTo(toValue);
		setFrom(fromValue);
		setDuration(duration);
		setFillBehavior(fillBehavior);
	}

	public double getBy() {
		return by;
	}

	public void setBy(double by) {
		this.by = by;
	}

	public double getFrom() {
		return from;
	}

	public void setFrom(double from) {
		this.from = from;
	}

	public double getTo() {
		return to;
	}

	public void setTo(double to) {
		this.to = to;
	}

	public boolean isAdditive() {
		return isAdditive;
	}

	public void setAdditive(boolean isAdditive) {
		this.isAdditive = isAdditive;
	}

	public boolean isCumulativeProperty() {
		return isCumulativeProperty;
	}

	public void setCumulativeProperty(boolean isCumulativeProperty) {
		this.isCumulativeProperty = isCumulativeProperty;
	}
	
	@Override
	protected void doStart(Timeline timeline, Object target) {		
	}
}
