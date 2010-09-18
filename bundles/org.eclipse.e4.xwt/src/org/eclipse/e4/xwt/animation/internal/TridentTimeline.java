/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.animation.internal;

import org.eclipse.e4.xwt.XWTException;
import org.eclipse.e4.xwt.animation.Duration;
import org.eclipse.e4.xwt.animation.IEasingFunction;
import org.eclipse.e4.xwt.animation.RepeatBehavior;
import org.eclipse.e4.xwt.animation.TimeSpan;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.TimelinePropertyBuilder;
import org.pushingpixels.trident.TimelinePropertyBuilder.DefaultPropertySetter;
import org.pushingpixels.trident.TimelineScenario.TimelineScenarioActor;

public class TridentTimeline implements ITimeline, TimelineScenarioActor {
	protected Timeline tridentTimeline;
	protected org.eclipse.e4.xwt.animation.Timeline xwtTimeline;
	protected Object target;
	private boolean isPlayed = false;
	private boolean wait = false;
	
	public TridentTimeline(org.eclipse.e4.xwt.animation.Timeline xwtTimeline, Object target) {
		this.xwtTimeline = xwtTimeline;
		this.target = target;
		this.tridentTimeline = createTimeline(target);
	}

	public Object getTarget() {
		return target;
	}

	public void play() {
		play(false);
	}

	public void play(boolean wait) {
		if (this.isPlayed) {
			this.tridentTimeline.replay();
		}
		else {
			Duration duration = this.xwtTimeline.getDuration();
			if (duration != null && duration.hasTimeSpan()) {
				this.tridentTimeline.setDuration(duration.getTimeSpan().getMilliseconds());
			} else {
				this.tridentTimeline.setDuration(10000);
			}
			RepeatBehavior behavior = xwtTimeline.getRepeatBehavior();
			playLoop(behavior);
			isPlayed = true;
		}
		while(wait && !this.tridentTimeline.isDone()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
	
	protected void setInitialValue() {
		
	}
	
	protected Timeline createTimeline(Object target) {
		org.pushingpixels.trident.Timeline timeline = new org.pushingpixels.trident.Timeline(target);
		Duration duration = this.xwtTimeline.getDuration();
		if (duration != null && duration.hasTimeSpan()) {
			timeline.setDuration(duration.getTimeSpan().getMilliseconds());
		} else {
			timeline.setDuration(10000);
		}
		return timeline;
	}
	
	public void playLoop(RepeatBehavior behavior) {
		org.pushingpixels.trident.Timeline.RepeatBehavior loopBehavior = org.pushingpixels.trident.Timeline.RepeatBehavior.LOOP;
		if (xwtTimeline.isAutoReverse()) {
			loopBehavior = org.pushingpixels.trident.Timeline.RepeatBehavior.REVERSE;
		}

		if (behavior.getHasCount()) {
			double loopCount = behavior.getCount();
			if (!behavior.getHasDuration()) {
				this.tridentTimeline.playLoop((int) loopCount, loopBehavior);
			} else {
				Duration duration = behavior.getDuration();
				this.tridentTimeline.playLoopSkipping((int) loopCount, loopBehavior,
						duration.getTimeSpan().getMilliseconds());
			}
		} else {
			if (!behavior.getHasDuration()) {
				this.tridentTimeline.playLoop(loopBehavior);
			} else {
				Duration duration = behavior.getDuration();
				this.tridentTimeline.playLoopSkipping(loopBehavior, duration.getTimeSpan()
						.getMilliseconds());
			}
		}		
	}

	public void end() {
		if (this.tridentTimeline == null) {
			return;
		}
		this.tridentTimeline.end();
	}

	public void cancel() {
		if (this.tridentTimeline == null) {
			return;
		}
		this.tridentTimeline.cancel();
	}

	public void abort() {
		if (this.tridentTimeline == null) {
			return;
		}
		this.tridentTimeline.abort();
	}

	
	public void pause() {
		if (this.tridentTimeline == null) {
			return;
		}
		this.tridentTimeline.suspend();
	}

	public void resume() {
		if (this.tridentTimeline == null) {
			return;
		}
		this.tridentTimeline.resume();
	}

	public void playReverse() {
		if (this.tridentTimeline == null) {
			return;
		}
		this.tridentTimeline.playReverse();
	}
	
	public final <T> void addPropertyToInterpolate(String propName, T from, T to) {
		if (to == null) {
			throw new XWTException("\"to\" property of Animation cannot be null.");
		}
		TimelinePropertyBuilder<T> builder = Timeline.<T> property(propName);
		if (from == null) {
			builder.fromCurrent();
		} else {
			builder.from(from);				
		}
		builder.to(to);
		int index = propName.indexOf('.');
		PathPropertyAccessor<T> propertyAccessor = null;
		if (index != -1) {
			propertyAccessor = new PathPropertyAccessor<T>(propName);
			builder.accessWith(propertyAccessor);
		}
		this.tridentTimeline.addPropertyToInterpolate(builder);
		
		TimeSpan timeSpan = this.xwtTimeline.getBeginTime();
		if (timeSpan != null && timeSpan.ticks != 0) {
			this.tridentTimeline.setInitialDelay(timeSpan.getMilliseconds());
		}
		else if (from != null){
			if (propertyAccessor != null) {
				propertyAccessor.set(target, propName, from);
			}
			else {
				DefaultPropertySetter<T> propertySetter = new DefaultPropertySetter<T>(target, propName);
				propertySetter.set(target, propName, from);
			}
		}
	}

	public void setEasingFunction(IEasingFunction easingFunction) {
		if (easingFunction != null) {
			this.tridentTimeline.setEase(easingFunction);
		}
	}
	
	public boolean isDone() {
		return tridentTimeline.isDone();
	}

	public void resetDoneFlag() {
		tridentTimeline.resetDoneFlag();
	}

	public boolean supportsReplay() {
		return tridentTimeline.supportsReplay();
	}
}
