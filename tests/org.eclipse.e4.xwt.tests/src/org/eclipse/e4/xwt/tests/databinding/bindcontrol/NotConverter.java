/**
 *
 */
package org.eclipse.e4.xwt.tests.databinding.bindcontrol;

import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.e4.xwt.IValueConverter;

/**
 * A Converter that converts boolean values with not operator
 *
 * @author hceylan
 *
 */
public class NotConverter extends Converter implements IValueConverter {

	/**
	 *
	 */
	public NotConverter() {
		super(Boolean.class, Boolean.class);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.databinding.conversion.IConverter#convert(java.lang.Object)
	 */
	public Object convert(Object fromObject) {
		return !((Boolean) fromObject);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.e4.xwt.IValueConverter#convertBack(java.lang.Object)
	 */
	public Object convertBack(Object value) {
		return this.convert(value);
	}

}
