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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.xwt.IValueValidator;

public class InverseValidator implements IValueValidator {

	private final IValueValidator delegate;

	public InverseValidator(IValueValidator delegate) {
		super();

		this.delegate = delegate;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.e4.xwt.IValueValidator#getBindingMode()
	 */
	public Direction getBindingMode() {
		switch (delegate.getBindingMode()) {
		case SourceToTarget:
			return Direction.TargetToSource;
		case TargetToSource:
			return Direction.SourceToTarget;
		case Both:
		default:
			return Direction.Both;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.e4.xwt.IValueValidator#getPhase()
	 */
	public Phase getPhase() {
		return delegate.getPhase();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.e4.xwt.IValueValidator#validateBack(java.lang.Object)
	 */
	public IStatus validateBack(Object value) {
		return delegate.validate(value);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.databinding.validation.IValidator#validate(java.lang.Object)
	 */
	public IStatus validate(Object value) {
		return delegate.validateBack(value);
	}

}
