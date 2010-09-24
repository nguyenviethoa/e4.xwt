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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.xwt.XWTException;
import org.eclipse.e4.xwt.animation.internal.AnimationManager;
import org.eclipse.e4.xwt.animation.internal.ITimeline;
import org.eclipse.e4.xwt.animation.internal.ITimelineGroup;
import org.eclipse.e4.xwt.animation.internal.ScenarioTimeline;
import org.eclipse.e4.xwt.animation.internal.TridentTimeline;
import org.eclipse.e4.xwt.annotation.Containment;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;
import org.pushingpixels.trident.TimelineScenario;

public class TimelineGroup extends Timeline {
	private Timeline[] children = EMPTY_ARRAY;

	private Map<Widget, ITimeline> timelines = new HashMap<Widget, ITimeline>();

	@Containment
	public Timeline[] getChildren() {
		return children;
	}

	public void setChildren(Timeline[] children) {
		this.children = children;
	}

	protected ITimelineGroup createTimelineGroup(Object target) {
		return new ScenarioTimeline(this, new TimelineScenario(), target);
	}

	public void start(final Event event, final Object target, Runnable endRunnable) {
		ITimeline timeline = timelines.get(event.widget);
		if (timeline == null) {
			timeline = createTimelineGroup(findTarget(target));
			updateTimeline(timeline, target);
			AnimationManager.getInstance().addTimeline(timeline);
			timelines.put(event.widget, timeline);
		}
		timeline.addStateChangedRunnable(endRunnable);
		AnimationManager.getInstance().play(timeline);
	}

	public void stop(Event event, Runnable stateChangedRunnable) {
		ITimeline timeline = timelines.get(event.widget);
		if (timeline != null) {
			timeline.addStateChangedRunnable(stateChangedRunnable);
			AnimationManager.getInstance().stop(timeline);
		}
	}

	public void pause(Event event, Runnable endRunnable) {
		ITimeline timeline = timelines.get(event.widget);
		if (timeline != null) {
			timeline.addStateChangedRunnable(endRunnable);
			AnimationManager.getInstance().pause(timeline);
		}
	}

	public void resume(Event event, Runnable endRunnable) {
		ITimeline timeline = timelines.get(event.widget);
		if (timeline != null) {
			timeline.addStateChangedRunnable(endRunnable);
			AnimationManager.getInstance().resume(timeline);
		}
	}

	public void playReverse(Event event, Runnable endRunnable) {
		ITimeline timeline = timelines.get(event.widget);
		if (timeline != null) {
			timeline.addStateChangedRunnable(endRunnable);
			AnimationManager.getInstance().playReverse(timeline);
		}
	}

	@Override
	protected void updateTimeline(ITimeline timeline, Object target) {
		super.updateTimeline(timeline, target);
		ITimelineGroup timelineGroup = (ITimelineGroup) timeline;
		HashMap<Object, TridentTimeline> map = new HashMap<Object, TridentTimeline>();
		
		for (Timeline child : children) {
			if (child instanceof ParallelTimeline) {
				TimelineScenario scenario = new TimelineScenario.Parallel();
				ScenarioTimeline scenarioTimeline = new ScenarioTimeline(child,
						scenario, child.findTarget(target));
				child.updateTimeline(scenarioTimeline, target);
				timelineGroup.addTimeline(scenarioTimeline);
			} else if (child instanceof TimelineGroup) {
				TimelineScenario scenario = new TimelineScenario.Sequence();
				ScenarioTimeline scenarioTimeline = new ScenarioTimeline(child,
						scenario, child.findTarget(target));
				child.updateTimeline(scenarioTimeline, target);
				timelineGroup.addTimeline(scenarioTimeline);
			} else {
				Object resolveTarget = child.findTarget(target);
				TridentTimeline tridentTimeline = map.get(resolveTarget);

				if (tridentTimeline == null) {
					if (!(resolveTarget instanceof Widget)) {
						throw new XWTException("The target of animation should be a Widget");
					}
					tridentTimeline = new TridentTimeline(child,
							(Widget) resolveTarget);
					timelineGroup.addTimeline(tridentTimeline);
					map.put(resolveTarget, tridentTimeline);
				}
				child.updateTimeline(tridentTimeline, target);
			}
		}
	}
}
