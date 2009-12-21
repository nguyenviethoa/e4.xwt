/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;

/**
 * @author hceylan
 *
 */
public interface IValueValidator extends IValidator {
	public static IValueValidator[] EMPTY_ARRAY = new IValueValidator[0];

	public enum Phase {
		AfterGet, //
		AfterConvert, //
		BeforeSet
	}

	public enum Direction {
		SourceToTarget, //
		TargetToSource, //
		Both
	}

	/**
	 * Returns the direction of the validation
	 *
	 * @return {@link Direction} indicating the direction of the validation.
	 *         Never null
	 */
	public Direction getBindingMode();

	/**
	 * Returns the phase this validator applies to
	 *
	 * @return {@link Phase} to indicate the phase of the validation. Never null
	 */
	public Phase getPhase();

	/**
	 * Determines if the given value is valid.
	 *
	 * @param value
	 *            the value to validate
	 * @return a status object indicating whether the validation succeeded
	 *         {@link IStatus#isOK()} or not. Never null.
	 */
	public IStatus validateBack(Object value);
}
