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

public abstract class Timeline {
	static public Timeline[] EMPTY_ARRAY = new Timeline[0];

	protected int desiredFrameRate = 0;
	protected double accelerationRatio = 0;
	protected boolean autoReverseProperty = false;
	protected TimeSpan beginTime = null;
	protected double decelerationRatio = 0;
	protected Duration duration = Duration.getAutomatic();
	protected FillBehavior fillBehavior = FillBehavior.HoldEnd;
	protected String name;
	protected double speedRatio = 0;
	protected RepeatBehavior repeatBehavior = RepeatBehavior.once;

	private org.pushingpixels.trident.Timeline timeline;

	public int getDesiredFrameRate() {
		return desiredFrameRate;
	}

	public void setDesiredFrameRate(int desiredFrameRate) {
		this.desiredFrameRate = desiredFrameRate;
	}

	public double getAccelerationRatio() {
		return accelerationRatio;
	}

	public void setAccelerationRatio(double accelerationRatio) {
		this.accelerationRatio = accelerationRatio;
	}

	public boolean isAutoReverseProperty() {
		return autoReverseProperty;
	}

	public void setAutoReverseProperty(boolean autoReverseProperty) {
		this.autoReverseProperty = autoReverseProperty;
	}

	public TimeSpan getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(TimeSpan beginTime) {
		this.beginTime = beginTime;
	}

	public double getDecelerationRatio() {
		return decelerationRatio;
	}

	public void setDecelerationRatio(double decelerationRatio) {
		this.decelerationRatio = decelerationRatio;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public FillBehavior getFillBehavior() {
		return fillBehavior;
	}

	public void setFillBehavior(FillBehavior fillBehavior) {
		this.fillBehavior = fillBehavior;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getSpeedRatio() {
		return speedRatio;
	}

	public void setSpeedRatio(double speedRatio) {
		this.speedRatio = speedRatio;
	}

	public RepeatBehavior getRepeatBehavior() {
		return repeatBehavior;
	}

	public void setRepeatBehavior(RepeatBehavior repeatBehavior) {
		this.repeatBehavior = repeatBehavior;
	}

	public void start(Object target) {
		timeline = new org.pushingpixels.trident.Timeline(findTarget(target));
		if (duration != null && duration.hasTimeSpan()) {
			timeline.setDuration(duration.getTimeSpan().getMilliseconds());
		} else {
			timeline.setDuration(10000);
		}
		doStart(timeline, target);
		playLoop(getRepeatBehavior());
	}

	protected Object findTarget(Object target) {
		return target;
	}

	public void stop() {
		if (timeline == null) {
			return;
		}
		timeline.abort();
		timeline = null;
	}

	public void pause() {
		if (timeline == null) {
			return;
		}
		timeline.suspend();
	}

	public void resume() {
		if (timeline == null) {
			return;
		}
		timeline.resume();
	}

	public void playReverse() {
		if (timeline == null) {
			return;
		}
		timeline.playReverse();
	}

	public void playLoop(RepeatBehavior behavior) {
		if (timeline == null) {
			return;
		}
		if (behavior.getHasCount()) {
			double loopCount = behavior.getCount();
			if (!behavior.getHasDuration()) {
				timeline.playLoop((int) loopCount,
						org.pushingpixels.trident.Timeline.RepeatBehavior.LOOP);
			} else {
				Duration duration = behavior.getDuration();
				timeline.playLoopSkipping((int) loopCount,
						org.pushingpixels.trident.Timeline.RepeatBehavior.LOOP,
						duration.getTimeSpan().getMilliseconds());
			}
		} else {
			if (!behavior.getHasDuration()) {
				timeline
						.playLoop(org.pushingpixels.trident.Timeline.RepeatBehavior.LOOP);
			} else {
				Duration duration = behavior.getDuration();
				timeline.playLoopSkipping(
						org.pushingpixels.trident.Timeline.RepeatBehavior.LOOP,
						duration.getTimeSpan().getMilliseconds());
			}
		}
	}

	protected abstract void doStart(
			org.pushingpixels.trident.Timeline timeline, Object target);
}
