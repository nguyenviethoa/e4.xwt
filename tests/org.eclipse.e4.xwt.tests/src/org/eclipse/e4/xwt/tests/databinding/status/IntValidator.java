/**
 *
 */
package org.eclipse.e4.xwt.tests.databinding.status;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.xwt.XWTException;
import org.eclipse.e4.xwt.validation.AbstractValidationRule;

/**
 * @author hceylan
 *
 */
public class IntValidator extends AbstractValidationRule {

	private String value;

	/**
	 *
	 */
	public IntValidator() {
		super();
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.core.databinding.validation.IValidator#validate(java.lang
	 * .Object)
	 */
	public IStatus validate(Object value) {
		if (this.value.equals(value)){
			return ValidationStatus.ok();
		}

		return ValidationStatus.error("Value must be " + this.value);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.e4.xwt.IValidationRule#validateBack(java.lang.Object)
	 */
	public IStatus validateBack(Object value) {
		return validate(value);
	}

}
