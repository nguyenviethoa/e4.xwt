package org.eclipse.e4.xwt;

import org.eclipse.core.databinding.conversion.IConverter;

/**
 * For a data binding, the conversion could be bidirectional. The standard IConverter is not enough. This class is designed to meet this requirement.
 * 
 * @author yyang
 * 
 */
public interface IValueConverter extends IConverter {
	Object convertBack(Object value);
}
