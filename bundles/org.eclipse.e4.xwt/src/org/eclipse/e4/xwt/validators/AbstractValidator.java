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
package org.eclipse.e4.xwt.validators;

import org.eclipse.e4.xwt.IValueValidator;

/**
 * Abstract implementation of {@link IValueValidator}
 *
 * @author hceylan
 */
public abstract class AbstractValidator implements IValueValidator {

	/**
	 *
	 */
	public AbstractValidator() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.e4.xwt.IValueValidator#getBindingMode()
	 */
	public Direction getBindingMode() {
		return Direction.Both;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.e4.xwt.IValueValidator#getPhase()
	 */
	public Phase getPhase() {
		return Phase.AfterGet;
	}

}
