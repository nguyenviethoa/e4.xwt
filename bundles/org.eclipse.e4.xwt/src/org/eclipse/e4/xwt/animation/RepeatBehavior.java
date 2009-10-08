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

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

public class RepeatBehavior {
	interface IFormatProvider {
	}

	RepeatBehavior() {
	}

	// Methods
	public RepeatBehavior(double count) {
		if ((Double.isInfinite(count) || Double.isNaN(count)) || (count < 0)) {
			throw new IllegalArgumentException("count: " + count);
		}
		try {
			this.repeatDuration = DatatypeFactory.newInstance().newDuration(0L);
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		this.iterationCount = count;
		this.type = RepeatBehaviorType.IterationCount;
	}

	public RepeatBehavior(javax.xml.datatype.Duration duration) {
		this.iterationCount = 0;
		this.repeatDuration = duration;
		this.type = RepeatBehaviorType.RepeatDuration;
	}

	public boolean equals(Object value) {
		if ((value instanceof RepeatBehavior)) {
			return this.Equals((RepeatBehavior) value);
		}
		return false;
	}

	public boolean Equals(RepeatBehavior repeatBehavior) {
		if (this.type == repeatBehavior.type) {
			switch (this.type) {
			case IterationCount: {
				return (this.iterationCount == repeatBehavior.iterationCount);
			}
			case RepeatDuration: {
				return (this.repeatDuration == repeatBehavior.repeatDuration);
			}
			case Forever: {
				return true;
			}
			}
		}
		return false;
	}

	public static boolean Equals(RepeatBehavior repeatBehavior1,
			RepeatBehavior repeatBehavior2) {
		return repeatBehavior1.Equals(repeatBehavior2);
	}

	public int hashCode() {
		switch (this.type) {
		case IterationCount: {
			return (int) this.iterationCount;
		}
		case RepeatDuration: {
			return this.repeatDuration.hashCode();
		}
		case Forever: {
			return 2147483605;
		}
		}
		return super.hashCode();
	}

	String InternalToString(String format, IFormatProvider formatProvider) {
		switch (this.type) {
		case IterationCount: {
			StringBuilder builder1 = new StringBuilder();
			// builder1.append(formatProvider, "{0:", format, "}x"), new
			// Object[] {
			// this.iterationCount
			// });
			return builder1.toString();
		}
		case RepeatDuration: {
			return this.repeatDuration.toString();
		}
		case Forever: {
			return "Forever";
		}
		}
		return null;
	}

	public static boolean op_Equality(RepeatBehavior repeatBehavior1,
			RepeatBehavior repeatBehavior2) {
		return repeatBehavior1.Equals(repeatBehavior2);
	}

	public static boolean op_Inequality(RepeatBehavior repeatBehavior1,
			RepeatBehavior repeatBehavior2) {
		return !repeatBehavior1.Equals(repeatBehavior2);
	}

	public String toString(String format, IFormatProvider formatProvider) {
		return this.InternalToString(format, formatProvider);
	}

	public String toString() {
		return this.InternalToString(null, null);
	}

	public String ToString(IFormatProvider formatProvider) {
		return this.InternalToString(null, formatProvider);
	}

	// Properties
	/**
	 * Property getter.
	 * 
	 * @property(Count)
	 */
	public double getCount() {
		if (this.type != RepeatBehaviorType.IterationCount) {
			throw new UnsupportedOperationException();
		}
		return this.iterationCount;
	}

	/**
	 * Property getter.
	 * 
	 * @property(Duration)
	 */
	public javax.xml.datatype.Duration getDuration() {
		if (this.type != RepeatBehaviorType.RepeatDuration) {
			throw new UnsupportedOperationException();
		}
		return this.repeatDuration;
	}

	/**
	 * Property getter.
	 * 
	 * @property(Forever)
	 */
	public static RepeatBehavior getForever() {
		RepeatBehavior behavior1 = new RepeatBehavior();
		behavior1.type = RepeatBehaviorType.Forever;
		return behavior1;
	}

	/**
	 * Property getter.
	 * 
	 * @property(HasCount)
	 */
	public boolean getHasCount() {
		return (this.type == RepeatBehaviorType.IterationCount);
	}

	/**
	 * Property getter.
	 * 
	 * @property(HasDuration)
	 */
	public boolean getHasDuration() {
		return (this.type == RepeatBehaviorType.RepeatDuration);
	}

	// Fields
	private double iterationCount;
	private javax.xml.datatype.Duration repeatDuration;
	private RepeatBehaviorType type;

	// Nested Types
	enum RepeatBehaviorType {
		IterationCount, RepeatDuration, Forever;
	}
}
