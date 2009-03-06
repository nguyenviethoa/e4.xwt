package org.eclipse.e4.xwt;

/**
 * 
 * @author yyang (yves.yang@soyatec.com)
 */
public interface IStyle {

	/**
	 * The element to apply the style. The argument is an instance created through XML. It can be a Widget, or others such as TableViewer.
	 * 
	 * @param target
	 */
	void applyStyle(Object target);
}
