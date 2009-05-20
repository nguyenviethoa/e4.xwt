package org.eclipse.e4.xwt;

/**
 * The opeation to inverse the converter direction.
 * 
 * @author yyang
 * 
 */
public class InverseValueConverter implements IValueConverter {
	private IValueConverter source;

	public InverseValueConverter(IValueConverter source) {
		this.source = source;
	}

	public Object convertBack(Object value) {
		return source.convert(value);
	}

	public Object convert(Object fromObject) {
		return source.convertBack(fromObject);
	}

	public Object getFromType() {
		return source.getToType();
	}

	public Object getToType() {
		return source.getFromType();
	}
}
