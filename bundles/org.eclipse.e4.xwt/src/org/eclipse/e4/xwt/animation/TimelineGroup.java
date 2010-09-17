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

import org.eclipse.e4.xwt.XWTMaps;
import org.eclipse.e4.xwt.animation.internal.AnimationManager;
import org.eclipse.e4.xwt.animation.internal.ITimeline;
import org.eclipse.e4.xwt.animation.internal.ITimelineGroup;
import org.eclipse.e4.xwt.animation.internal.ScenarioTimeline;
import org.eclipse.e4.xwt.animation.internal.TridentTimeline;
import org.eclipse.e4.xwt.annotation.Containment;
import org.eclipse.e4.xwt.core.EventTrigger;
import org.eclipse.e4.xwt.core.Trigger;
import org.eclipse.swt.widgets.Event;
import org.pushingpixels.trident.TimelineScenario;

public class TimelineGroup extends Timeline {
	private Timeline[] children = EMPTY_ARRAY;
	
	private ITimeline timeline;

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
	
	public void start(Event event, Object target) {
		if (timeline == null) {
			timeline = createTimelineGroup(findTarget(target));
			updateTimeline(timeline, target);
			AnimationManager.getInstance().addTimeline(timeline);
		}
		if (event.type == XWTMaps.getEvent("swt.dispose")) {
			AnimationManager.getInstance().play(timeline, false);
			event.doit = true;
		}
		else {
			AnimationManager.getInstance().play(timeline, false);			
		}
	}
	
	public void stop(Event event) {
		AnimationManager.getInstance().stop(timeline);
	}

	public void pause(Event event) {
		AnimationManager.getInstance().pause(timeline);
	}

	public void resume(Event event) {
		AnimationManager.getInstance().resume(timeline);
	}

	public void playReverse(Event event) {
		AnimationManager.getInstance().playReverse(timeline);
	}

	@Override
	protected void updateTimeline(ITimeline timeline, Object target) {
		super.updateTimeline(timeline, target);
		ITimelineGroup timelineGroup = (ITimelineGroup) timeline;
		for (Timeline child : children) {
			if (child instanceof ParallelTimeline) {
				TimelineScenario scenario = new TimelineScenario.Parallel();
				ScenarioTimeline scenarioTimeline = new ScenarioTimeline(child, scenario, child.findTarget(target));
				child.updateTimeline(scenarioTimeline, target);
				timelineGroup.addTimeline(scenarioTimeline);
			} else if (child instanceof TimelineGroup) {
				TimelineScenario scenario = new TimelineScenario.Sequence();
				ScenarioTimeline scenarioTimeline = new ScenarioTimeline(child, scenario, child.findTarget(target));
				child.updateTimeline(scenarioTimeline, target);
				timelineGroup.addTimeline(scenarioTimeline);
			}
			else {	
				TridentTimeline tridentTimeline = new TridentTimeline(child, child.findTarget(target));
				timelineGroup.addTimeline(tridentTimeline);
				child.updateTimeline(tridentTimeline, target);
			}
		}
	}
}
