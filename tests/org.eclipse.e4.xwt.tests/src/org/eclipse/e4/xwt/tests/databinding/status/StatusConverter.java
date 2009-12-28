package org.eclipse.e4.xwt.tests.databinding.status;

import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.xwt.IValueConverter;

public class StatusConverter extends Converter implements IValueConverter {

	/**
	 *
	 */
	public StatusConverter() {
		super(Object.class, String.class);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.e4.xwt.IValueConverter#convertBack(java.lang.Object)
	 */
	public Object convertBack(Object value) {
		return ValidationStatus.error((String) value);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.databinding.conversion.IConverter#convert(java.lang.Object)
	 */
	public Object convert(Object fromObject) {
		IStatus status = (IStatus) fromObject;

		if (fromObject == null){
			return ""; //$NON-NLS-1$
		}

		return status.getMessage();
	}
}
