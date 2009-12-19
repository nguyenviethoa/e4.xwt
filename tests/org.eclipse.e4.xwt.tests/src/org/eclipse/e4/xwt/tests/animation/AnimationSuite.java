package org.eclipse.e4.xwt.tests.animation;

import org.eclipse.e4.xwt.tests.animation.repeatBehavior.RepeatBehaviorTests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AnimationSuite extends TestSuite {
	public static final Test suite() {
		return new AnimationSuite();
	}

	public AnimationSuite() {
		addTest(new TestSuite(AnimationTests.class));
		addTest(new TestSuite(RepeatBehaviorTests.class));
	}
}
